import csv

import psycopg2
import time

from psycopg2.extras import execute_values

HOST = "localhost"
DATABASE = "project1"
USER = "postgres"
PASSWORD = "123456"
PORT = "5432"
OPEN_CLOSE_TIME = 10
BATCH_SIZE = 1000
'''It means testing open and close database 10 tims'''


def test_connect():
    con = None
    try:
        start = time.time()
        for i in range(OPEN_CLOSE_TIME):
            con = psycopg2.connect(database=DATABASE, user=USER, password=PASSWORD, host=HOST, port=PORT)
            con.close()
        end = time.time()
        execution_time = (end - start) * 1000
        execution_time = round(execution_time, 2)
        print("Connect successfully.")
        print(f"Open and close db {OPEN_CLOSE_TIME} tims use {execution_time} ms")
    except(Exception, psycopg2.DatabaseError) as error:
        print(error)


def test_insert():
    con = None
    try:
        con = psycopg2.connect(database=DATABASE, user=USER, password=PASSWORD, host=HOST, port=PORT)
        cur = con.cursor()
        sql = "truncate table project_user cascade"
        cur.execute(sql)
        con.commit()
        cur.close()
        con.close()
    except(Exception, psycopg2.DatabaseError) as error:
        print(error)
    con = None
    try:
        con = psycopg2.connect(database=DATABASE, user=USER, password=PASSWORD, host=HOST, port=PORT)
        cur = con.cursor()
        with open('..\\..\\data\\users.csv', 'r', encoding='utf-8') as f:
            reader = csv.reader(f)
            next(reader)
            rows = [(row[0], row[1], row[2], row[3], row[4], row[5], row[7]) for row in reader]
        start = time.time()
        for i in range(0, len(rows), 1000):
            batch = rows[i:i + 1000]
            execute_values(cur, "insert into project_user (mid, name, sex, birthday ,level, sign, identity) VALUES %s", batch)
        con.commit()
        end = time.time()
        cur.close()
        con.close()
        execution_time = (end - start) * 1000
        execution_time = round(execution_time, 2)
        print(f"Insert use {execution_time} ms")
    except(Exception, psycopg2.DatabaseError) as error:
        print(error)


def test_query():
    con = None
    try:
        con = psycopg2.connect(database=DATABASE, user=USER, password=PASSWORD, host=HOST, port=PORT)
        cur = con.cursor()
        sql = "select mid from project_user"
        start = time.time()
        cur.execute(sql)
        rows = cur.fetchall()
        end =time.time()
        execution_time = (end - start) * 1000
        execution_time = round(execution_time, 2)
        print(f"Query mid for all users: {execution_time} ms")
    except(Exception, psycopg2.DatabaseError) as e:
        print(e)
    con = None
    try:
        con = psycopg2.connect(database=DATABASE, user=USER, password=PASSWORD, host=HOST, port=PORT)
        cur = con.cursor()
        sql = "select mid from project_user where level > %s"
        params = (3,)
        start = time.time()
        cur.execute(sql, params)
        rows = cur.fetchall()
        end = time.time()
        execution_time = (end - start) * 1000
        execution_time = round(execution_time, 2)
        print(f"Query the mid of all users whose level is greater than 3: {execution_time} ms")
    except(Exception, psycopg2.DatabaseError) as e:
        print(e)
    con = None
    try:
        con = psycopg2.connect(database=DATABASE, user=USER, password=PASSWORD, host=HOST, port=PORT)
        cur = con.cursor()
        sql = "select mid from project_user where cast(mid as varchar) like '24%'"
        start = time.time()
        cur.execute(sql)
        rows = cur.fetchall()
        end = time.time()
        execution_time = (end - start) * 1000
        execution_time = round(execution_time, 2)
        print(f"Query the mid of all users whose mid starts with 24: {execution_time} ms")
    except(Exception, psycopg2.DatabaseError) as e:
        print(e)


if __name__ == "__main__":
    test_query()
