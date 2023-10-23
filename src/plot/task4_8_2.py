import matplotlib.pyplot as plt

x = [20000, 40000, 60000, 80000, 100000]
a = [1863, 3286, 4978, 6451, 8665]
b = [796, 1192, 1756, 2358, 2877]
c = [556, 834, 1237, 1582, 1919]
d = [366, 615, 942, 1188, 1530]
e = [152, 283, 813, 974, 1059]

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

