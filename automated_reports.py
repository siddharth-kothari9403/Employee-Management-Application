from email.mime.application import MIMEApplication
from mysql.connector import Error
import pandas as pd
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from datetime import date
from sqlalchemy import create_engine
from dotenv import load_dotenv
import os
import matplotlib.pyplot as plt
import seaborn as sns

def convert_to_hours(duration_str):
    if pd.isna(duration_str): return 0
    parts = duration_str.split()
    hours, minutes = 0, 0
    for part in parts:
        if 'h' in part:
            hours = int(part.replace('h', ''))
        elif 'm' in part:
            minutes = int(part.replace('m', ''))
    return hours + (minutes / 60.0)

def plot_daily_work_hours(summary, monday_date, friday_date):

    summary['Hours'] = summary['Work Duration'].apply(convert_to_hours)
    summary['Full Name'] = summary['First Name'] + ' ' + summary['Last Name']

    # 3. Create the grouped bar chart
    plt.figure(figsize=(16, 8))
    sns.barplot(data=summary, x='Full Name', y='Hours', hue='Date')

    # Formatting
    plt.xticks(rotation=45, ha='right')
    plt.title(f'Work Duration per Employee by Date ({monday_date} to {friday_date})', fontsize=16)
    plt.ylabel('Hours Worked', fontsize=12)
    plt.xlabel('Employee Name', fontsize=12)
    plt.legend(title='Date', bbox_to_anchor=(1.05, 1), loc='upper left')
    plt.tight_layout()

    # Save and show
    plt.savefig('daily_work_duration_plot.png')
    
def plot_department_hours(dept_analysis, monday_date, friday_date):
    plt.figure(figsize=(10, 6))
    plt.bar(dept_analysis['Department'], dept_analysis['Average Hours'], color='skyblue')
    plt.xlabel('Department')
    plt.ylabel('Average Hours')
    plt.title(f'Average Hours Worked by Department ({monday_date} to {friday_date})')
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.savefig('department_hours.png')

def plot_employee_hours(employee_report, monday_date, friday_date):
    plt.figure(figsize=(12, 8))
    employee_report = employee_report.sort_values(by='Employee Total Hours', ascending=False)
    plt.barh(employee_report['First Name'] + ' ' + employee_report['Last Name'], employee_report['Employee Total Hours'], color='lightgreen')
    plt.xlabel('Total Hours Worked')
    plt.title(f'Total Hours Worked by Employee ({monday_date} to {friday_date})')
    plt.tight_layout()
    plt.savefig('employee_hours.png')

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
        summary.sort_values(by=['Date', 'Department', 'First Name', 'Last Name'], inplace=True)
        summary = summary[['Date', 'Department', 'First Name', 'Last Name', 'Work Duration', 'Login Time', 'Logout Time', 'Duration', 'Duration in Seconds',]]
        summary.to_csv('daily_report.csv', columns=['Date', 'Department', 'First Name', 'Last Name', 'Work Duration'], index=False)

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
        employee_report.to_csv('employee_report.csv', columns=['First Name', 'Last Name', 'Department', 'Employee Total Hours'], index=False)

        plot_daily_work_hours(summary, monday_date, friday_date)
        plot_department_hours(dept_analysis, monday_date, friday_date)
        plot_employee_hours(employee_report, monday_date, friday_date)

    except Error as e:
        print(f"Error connecting to MySQL: {e}")
        return None, None
    
def send_email(sender_email, receiver_email, password, files=None):

    message = MIMEMultipart("alternative")
    message["Subject"] = f"Company Productivity Report: {date.today()}"
    body = "PFA the daily attendance and departmental reports attached as CSV files."
    message.attach(MIMEText(body, "plain"))

    for f in files or []:
        with open(f, "rb") as fil:
            part = MIMEApplication(
                fil.read(),
                Name=os.path.basename(f)
            )
        # After the file is closed
        part['Content-Disposition'] = f'attachment; filename={os.path.basename(f)}'
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

    generate_report(db_host, db_port, db_name, db_username, db_password, monday_date='2024-05-06', friday_date='2024-05-10')
    send_email(sender_email, receiver_email, password, files=['daily_report.csv', 'department_report.csv', 'employee_report.csv', 'daily_work_duration_plot.png', 'department_hours.png', 'employee_hours.png'])
    print("Success: Report sent.")