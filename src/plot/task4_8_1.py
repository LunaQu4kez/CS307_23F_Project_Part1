import matplotlib.pyplot as plt
import numpy as np

size = 5
a = [7813, 9907, 9750, 7939, 7914]
b = [8939, 8454, 8509, 8466, 8478]
c = [8485, 8405, 8447, 8485, 8456]
d = [8602, 8542, 8705, 8533, 8658]
e = [8738, 8587, 8755, 8618, 8790]

total_width, n = 0.8, 5
width = total_width / n
x = np.arange(1, size + 1)
x = x - (total_width - width) / 2

plt.bar(x, a, width=width, label="1 Thread")
plt.bar(x + width, b, width=width, label="5 Threads")
plt.bar(x + 2*width, c, width=width, label="10 Threads")
plt.bar(x + 3*width, d, width=width, label="20 Threads")
plt.bar(x + 4*width, e, width=width, label="50 Threads")

for i, j in zip(x, a):
    plt.text(i, j + 0.01, "%d" % j, ha="center", va="bottom", fontsize=6)
for i, j in zip(x + width, b):
    plt.text(i, j + 0.01, "%d" % j, ha="center", va="bottom", fontsize=6)
for i, j in zip(x + 2*width, c):
    plt.text(i, j + 0.01, "%d" % j, ha="center", va="bottom", fontsize=6)
for i, j in zip(x + 3*width, d):
    plt.text(i, j + 0.01, "%d" % j, ha="center", va="bottom", fontsize=6)
for i, j in zip(x + 4*width, e):
    plt.text(i, j + 0.01, "%d" % j, ha="center", va="bottom", fontsize=6)

plt.ylim(0, 12000)

plt.xlabel("No. Test")
plt.ylabel("Time Cost/ms")
plt.title("Time Cost when Number of Data is 100000")
plt.legend()
plt.show()
