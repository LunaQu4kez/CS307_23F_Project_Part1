import matplotlib.pyplot as plt

x = ['1', '2', '3', '4', '5']
a = [113, 106, 105, 115, 111]  # file io
b = [198, 172, 177, 187, 187]  # database


plt.plot(x, a, label='File I/O', marker='o')
plt.plot(x, b, label='Database', marker='x')

for i, j in zip(x, a):
    plt.text(i, j + 0.1, "%d" % j, ha="center", va="bottom", fontsize=10)
for i, j in zip(x, b):
    plt.text(i, j + 0.1, "%d" % j, ha="center", va="bottom", fontsize=10)

plt.ylim(0, 250)

plt.xlabel("Test No.")
plt.ylabel("Time Cost/ms")
plt.title("")
plt.legend()
plt.show()
