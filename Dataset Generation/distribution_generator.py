"""
This script is used to create datasets composed of random values
following normal, uniform, correlated and anticorrelated distribution respectively.
Authors: Georgitsis Christos, Dialektakis George
Date: 30/11/2020
"""

import numpy as np
import matplotlib.pyplot as plt
from sklearn import preprocessing

# parameters of dataset
size = 10000000
dimension = 2

scaler = preprocessing.MinMaxScaler()

############################ Creating dataset with uniform distribution ############################

uniform_array = np.random.uniform(0, 1, [size, dimension])
# Normalizing data between 0 and 1
uniform_scaled = scaler.fit_transform(uniform_array)

# saving data into a csv file
filename = "uniform_%s.csv" % dimension
np.savetxt(filename, uniform_scaled, fmt="%f", delimiter=",")
plt.title("Uniform")
plt.scatter(uniform_scaled[:, 0], uniform_scaled[:, 1])

############################ Creating dataset with normal distribution ############################

normal_array = np.random.normal(0, 1, [size, dimension])
# Normalizing data between 0 and 1
normal_scaled = scaler.fit_transform(normal_array)

# saving data into a csv file
filename = "normal_%s.csv" % dimension
np.savetxt(filename, normal_scaled, fmt="%f", delimiter=",")

plt.figure()
plt.title("Normal")
plt.scatter(normal_scaled[:, 0], normal_scaled[:, 1])

############################ Creating dataset with correlated distribution ############################

# Positive Correlation with some noise
y = [np.random.random(size)]
print(np.random.random(size))

for j in range(0, dimension - 1):
    y.append(y[0] + np.random.normal(0.5, 0.3, size))

corr_array = np.array(y)
corr_array = corr_array.T

# Normalizing data between 0 and 1
corr_scaled = scaler.fit_transform(corr_array)

# saving data into a csv file
filename = "correlated_%s.csv" % dimension
np.savetxt(filename, corr_scaled, fmt="%f", delimiter=",")

plt.figure()
plt.title("Correlated")
plt.scatter(corr_scaled[0], corr_scaled[1])

############################ Creating dataset with anticorrelated distribution ############################

# anticorrelated only for one dimension and more specific for y-->dimension
y = [np.random.random(size)]

for j in range(0, dimension - 1):
    if j == 0:
        y.append(1 - y[0] + np.random.normal(0.5, 0.3, size))
    else:
        y.append(y[1] + np.random.normal(0.5, 0.3, size))

anticorr_array = np.array(y)
anticorr_array = anticorr_array.T

# Normalizing data between 0 and 1
anticorr_scaled = scaler.fit_transform(anticorr_array)

# saving data into a csv file
filename = "anticorrelated_%s.csv" % dimension
np.savetxt(filename, anticorr_scaled, fmt="%f", delimiter=",")

plt.figure()
plt.title("Anticorrelated")
plt.scatter(anticorr_scaled[:, 0], anticorr_scaled[:, 1])
#plt.show()