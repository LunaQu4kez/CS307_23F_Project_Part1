import psycopg2
import time

HOST = "localhost"
DATABASE = "CS307_proj1"
USER = "postgres"
PASSWORD = "12211655"
PORT = "5432"


def connect():
    con = None
    try:
        start = time.time()
        con = psycopg2.connect(database=DATABASE, user=USER, password=PASSWORD, host=HOST, port=PORT)
        con.close()
        end = time.time()
        execution_time = (end - start) * 1000
        execution_time = round(execution_time, 2)
        print("Connect successfully.")
        print(f"Use  {execution_time} ms")
    except(Exception, psycopg2.DatabaseError) as error:
        print(error)

if __name__  == "__main__":
    connect()
