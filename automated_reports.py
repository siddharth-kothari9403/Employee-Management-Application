from email.mime.application import MIMEApplication
import mysql.connector
from mysql.connector import Error
import pandas as pd
import smtplib
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from email import encoders
from email.mime.multipart import MIMEMultipart
from datetime import date
from sqlalchemy import create_engine
import io
from dotenv import load_dotenv
import os

def generate_report(db_host, db_port, db_name, db_username, db_password, monday_date, friday_date):
    try: 
        engine = create_engine(f"mysql+pymysql://{db_username}:{db_password}@{db_host}:{db_port}/{db_name}")

        query = f"""
        SELECT e.first_name, e.last_name, e.department, l.date, l.time, l.entry_type
        FROM employee_details e
        JOIN login_logout_times l ON e.employee_id = l.employee_id
        where l.date between "{monday_date}" and "{friday_date}"
        ORDER BY e.employee_id, l.time;
        """
        
        df = pd.read_sql(query, engine)
        engine.dispose()

        df['time'] = pd.to_timedelta(df['time'].astype(str))
        if df.empty:
            return None, None
        
        df['pair_id'] = df.groupby(['first_name', 'date', 'entry_type']).cumcount()

        pivoted = df.pivot_table(
            index=['first_name', 'last_name', 'department', 'date', 'pair_id'], 
            columns='entry_type', 
            values='time',
        ).reset_index()

        summary = pivoted.groupby(['first_name', 'last_name', 'department', 'date']).agg({
            'login': 'min',
            'logout': 'max'
        }).reset_index()

        # If logout is missing (NaN), fill with 17:00:00 (5 PM)
        if 'logout' not in summary.columns:
            summary['logout'] = pd.to_timedelta('17:00:00')
        else:
            summary['logout'] = summary['logout'].fillna(pd.to_timedelta('17:00:00'))
        
        summary['duration'] = summary['logout'] - summary['login']
        summary['duration_sec'] = (summary['logout'] - summary['login']).dt.total_seconds()
        summary['Work Duration'] = summary['duration'].apply(
            lambda x: f"{int(x.total_seconds() // 3600)}h {int((x.total_seconds() // 60) % 60)}m"
        )

        summary.columns = ['First Name', 'Last Name', 'Department', 'Date', 'Login Time', 'Logout Time', 'Duration', 'Duration in Seconds', 'Work Duration']

        print(summary)
        summary.to_csv('daily_report.csv', columns=['First Name', 'Last Name', 'Department', 'Date', 'Work Duration'], index=False)

        dept_analysis = summary.groupby('Department')['Duration in Seconds'].agg(['sum', 'mean', 'count']).reset_index()
        dept_analysis.columns = ['Department', 'Total Seconds', 'Average Seconds', 'Employee Count']

        # Convert seconds to decimal hours
        dept_analysis['Total Hours'] = (dept_analysis['Total Seconds'] / 3600).round(2)
        dept_analysis['Average Hours'] = (dept_analysis['Average Seconds'] / 3600).round(2)
        print(dept_analysis)

        dept_analysis.to_csv('department_report.csv', columns=['Department', 'Total Hours', 'Average Hours'], index=False)

        employee_report = summary.groupby(['First Name', 'Last Name', 'Department'])['Duration'].sum().reset_index()
        employee_report['Employee Total Hours'] = employee_report['Duration'].apply(
            lambda x: round(x.total_seconds() / 3600, 2)
        )
        employee_report.columns = ['First Name', 'Last Name', 'Department', 'Duration', 'Employee Total Hours']
        
        print(employee_report)
        employee_report.to_csv('employee_report.csv', index=False)

        return summary[['First Name', 'Last Name', 'Department', 'Date', 'Work Duration']], \
           dept_analysis[['Department', 'Total Hours', 'Average Hours']], \
           employee_report[['First Name', 'Last Name', 'Department', 'Employee Total Hours']]

    except Error as e:
        print(f"Error connecting to MySQL: {e}")
        return None, None
    
def send_email(sender_email, receiver_email, password, indiv_df, dept_df, emp_df):

    message = MIMEMultipart("alternative")
    message["Subject"] = f"Company Productivity Report: {date.today()}"
    body = "PFA the daily attendance and departmental reports attached as CSV files."
    message.attach(MIMEText(body, "plain"))

    for df, filename in [(indiv_df, "daily_report.csv"), (dept_df, "department_report.csv"), (emp_df, "employee_report.csv")]:
        # Convert DF to CSV in memory
        buffer = io.StringIO()
        df.to_csv(buffer, index=False)
        buffer.seek(0)

        # Create the attachment object
        part = MIMEBase("application", "octet-stream")
        part.set_payload(buffer.getvalue())
        encoders.encode_base64(part)
        part.add_header("Content-Disposition", f"attachment; filename={filename}")
        message.attach(part)

    with smtplib.SMTP_SSL("smtp.gmail.com", 465) as server:
        server.login(sender_email, password)
        server.sendmail(sender_email, receiver_email, message.as_string())

if __name__ == "__main__":
    load_dotenv()
    db_host = os.getenv('DB_HOST')
    db_port = os.getenv('DB_PORT')
    db_name = os.getenv('DB_NAME')
    db_username = os.getenv('DB_USERNAME')
    db_password = os.getenv('DB_PASSWORD')
    sender_email = os.getenv('SENDER_EMAIL')
    receiver_email = os.getenv('RECEIVER_EMAIL')
    password = os.getenv('SENDER_PASSWORD')

    daily_report, dept_report, emp_report = generate_report(db_host, db_port, db_name, db_username, db_password, monday_date='2024-05-06', friday_date='2024-05-10')
    send_email(sender_email, receiver_email, password, daily_report, dept_report, emp_report)
    print("Success: Report sent.")