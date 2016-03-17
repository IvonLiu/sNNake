import numpy as np
import neuralnetwork as nn
import config
import csv
import time


def currentTimeMillis():
    millis = int(round(time.time() * 1000))
    return millis

def train(net, X, y):
    # Cost before training
    cost1 = net.costFunction(X, y)

    # Begin training. Get smarter!!
    if config.train == "simple":
        simpleTrain(net, X, y)
    elif config.train == "bfgs":
        bfgs(net, X, y)

    # Cost after training
    cost2 = net.costFunction(X, y)

    print("Decreased cost by ", cost1 - cost2, ", from ", cost1, " to ", cost2, "using ", config.train)

def simpleTrain(net, X, y):
    scalar = 3
    for i in range(0, 2000):
        print("Training iteration {}".format(i))
        dJdW = list(net.costFunctionPrime(X, y))
        for j in range(len(net.W)):
            net.W[j] -= scalar * dJdW[j]

def bfgs(net, X, y):
    charmer = nn.Trainer(net)
    charmer.train(X, y)


'''
Open the currently active example set
and use it to train the neural net.
Save the tuned weights into an output
folder once training is complete.
'''

with open('../TrainingData/active/examples{}.csv'.format(config.suffix), 'r') as f:
    reader = csv.reader(f)
    examples = np.array(list(reader), dtype=float)

X = examples[:, :config.inputLayerSize]
y = examples[:, -config.outputLayerSize:]

# Hyperparameters
inputLayerSize = config.inputLayerSize
outputLayerSize = config.outputLayerSize
hiddenLayerSize = config.hiddenLayerSize

# Regularization
lambd = config.lambd

# Configure activation function
useAct = config.useAct

# Instantiate and train neural net
net = nn.NeuralNetwork(inputLayerSize, outputLayerSize, hiddenLayerSize, lambd, useAct)
train(net, X, y)

# Save weights to csv
params = net.getParams()
np.savetxt('../Parameters/weights_{}{}.csv'.format(currentTimeMillis(), config.suffix), params, delimiter=",", fmt='%s')