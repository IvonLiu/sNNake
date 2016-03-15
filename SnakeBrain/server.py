import numpy as np
import math
import neuralnetwork as nn
from flask import Flask
from flask import request
from flask import jsonify


app = Flask(__name__)


####################################
########## Helper methods ##########
####################################

def generateTrainingSet(n, xMin, xMax, yMax, maxNoise):
    X = np.random.randint(xMin, high=xMax + 1, size=[n, 2])
    X = np.multiply(X, 1.0)

    y = np.empty([n, 1], dtype=float)
    for i in range(0, n):
        # Calculates product +/- maxNoise
        y[i] = [(X[i][0] * X[i][1] + np.random.randint(-maxNoise, high=maxNoise + 1))]

    return X, y

def train(net, X, y):
    # Cost before training
    cost1 = net.costFunction(X, y)

    # Begin training. Get smarter!!
    # badTrain(net, X, y)
    smartTrain(net, X, y)

    # Cost after training
    cost2 = net.costFunction(X, y)

    print("Decreased cost by ", cost1 - cost2, ", from ", cost1, " to ", cost2)

def smartTrain(net, X, y):
    T = nn.SnakeCharmer(net)
    T.train(X, y)

def printResults(X, yHat, xNorm, yNorm):
    for i in range(0, min(10, len(X))):
        print('{:> 10.3f} * {:> 10.3f} = {:> 10.0f} ({:> 10.3f})'.format(
            X[i][0] * xNorm[0],
            X[i][1] * xNorm[1],
            yHat[i][0] * yNorm,
            X[i][0] * xNorm[0] * X[i][1] * xNorm[1]
        ))


###########################################
########## Initialize neural net ##########
###########################################

# Hyperparameters
inputLayerSize = 2
outputLayerSize = 1
hiddenLayerSize = [3, 3]

# Regularization
lambd = 0.0001

# Configure activation function
useAct = "softRelu"
xNorm = [50, 50]
yNorm = 50

net = nn.NeuralNetwork(inputLayerSize, outputLayerSize, hiddenLayerSize, lambd, useAct)

# Generate Training Set For Multiplication with (Soft) ReLU

# Training set generation parameters
n = 100
yMax = 1000
maxNoise = 10

xMin = math.ceil(maxNoise ** 0.5)
xMax = math.floor((yMax - maxNoise) ** 0.5)

X, y = generateTrainingSet(n, xMin, xMax, yMax, maxNoise)

X = X / xNorm
y = y / yNorm

train(net, X, y)


#########################################
########## Route configuration ##########
#########################################

@app.route("/forward", methods=['POST'])
def forward():
    json = request.get_json()
    X = np.array(json['X'], dtype=float)
    X = X / xNorm
    yHat = net.forward(X) * yNorm
    return jsonify({'yHat': yHat.tolist()})

if __name__ == "__main__":
    app.run(debug=True)