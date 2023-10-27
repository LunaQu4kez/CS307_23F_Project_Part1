import matplotlib.pyplot as plt

x = ['update', 'select', 'delete']  #1. update 2.select 3.delete
a = [19, 53, 54]  # postgresql
b = [27, 108, 14]  # mysql


plt.plot(x, a, label='Postgresql', marker='o')
plt.plot(x, b, label='Mysql', marker='x')

for i, j in zip(x, a):
    plt.text(i, j + 0.1, "%d" % j, ha="center", va="bottom", fontsize=10)
for i, j in zip(x, b):
    plt.text(i, j + 0.1, "%d" % j, ha="center", va="bottom", fontsize=10)

plt.ylim(0, 120)

plt.xlabel("Operation")
plt.ylabel("Time Cost/ms")
plt.title("")
plt.legend()
plt.show()
