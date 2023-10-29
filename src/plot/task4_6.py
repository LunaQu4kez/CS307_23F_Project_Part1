import matplotlib.pyplot as plt
import numpy as np

size = 3
a = [19, 53, 54]
b = [27, 108, 14]


total_width, n = 0.6, 2
width = total_width / n
x = np.arange(1, size + 1)
x = x - (total_width - width) / 2

plt.bar(x, a, width=width, label="Postgresql")
plt.bar(x + width, b, width=width, label="MySQL")

for i, j in zip(x, a):
    plt.text(i, j + 0.01, "%d" % j, ha="center", va="bottom", fontsize=10)
for i, j in zip(x + width, b):
    plt.text(i, j + 0.01, "%d" % j, ha="center", va="bottom", fontsize=10)

plt.ylim(0, 120)

plt.xticks(x + width / 2, ['Update', 'Select', 'Delete'])
plt.xlabel("Operation")
plt.ylabel("Time Cost/ms")
plt.title("Postgresql vs MySQL")
plt.legend()
plt.show()
