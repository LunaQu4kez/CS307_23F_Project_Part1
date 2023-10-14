import matplotlib.pyplot as plt

x = [20000, 40000, 60000, 80000, 100000]
a = [1671, 3131, 4978, 6451, 8665]
b = [1729, 3355, 5016, 6688, 8569]
c = [1776, 3386, 5056, 6651, 8455]
d = [1768, 3451, 5078, 6732, 8608]
e = [1785, 3465, 5149, 6741, 8698]

plt.plot(x, a, label='1 Thread', marker='.')
plt.plot(x, b, label='5 Threads', marker='x')
plt.plot(x, c, label='10 Threads', marker='o')
plt.plot(x, d, label='20 Threads', marker='+')
plt.plot(x, e, label='50 Threads', marker='*')


plt.xlabel("Data Size")
plt.ylabel("Time Cost/ms")
plt.title("Time Cost in Different Data Size")
plt.legend()
plt.show()

