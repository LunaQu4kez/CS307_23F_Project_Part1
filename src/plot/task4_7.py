import matplotlib.pyplot as plt
import numpy as np

size = 3
a = [27, 9, 6]
b = [13, 11, 7]


total_width, n = 0.6, 2
width = total_width / n
x = np.arange(1, size + 1)
x = x - (total_width - width) / 2

plt.bar(x, a, width=width, label="JDBC")
plt.bar(x + width, b, width=width, label="PDBC")

for i, j in zip(x, a):
    plt.text(i, j + 0.01, "%d" % j, ha="center", va="bottom", fontsize=10)
for i, j in zip(x + width, b):
    plt.text(i, j + 0.01, "%d" % j, ha="center", va="bottom", fontsize=10)

plt.ylim(0, 30)

plt.xticks(x + width / 2, ['1', '2', '3'])
plt.xlabel("Statement No.")
plt.ylabel("Time Cost/ms")
plt.title("JDBC vs PDBC")
plt.legend()
plt.show()
