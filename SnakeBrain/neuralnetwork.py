import numpy as np
from scipy import optimize


class NeuralNetwork(object):
    def __init__(self, inputLayerSize, outputLayerSize, hiddenLayerSize, lambd=0.0001, useAct="sigmoid"):
        # Define Hyperparameters
        self.inputLayerSize = inputLayerSize
        self.outputLayerSize = outputLayerSize
        self.layerSizes = hiddenLayerSize
        self.layerSizes.insert(0, inputLayerSize)
        self.layerSizes.append(outputLayerSize)

        # Weights
        self.W = [0] * (len(self.layerSizes) - 1)
        for i in range(0, len(self.W)):
            self.W[i] = np.random.randn(self.layerSizes[i], self.layerSizes[i + 1])

        # Regularization
        self.lambd = lambd

        # Activation funciton
        self.useAct = useAct

    def forward(self, X):
        self.z = [0] * len(self.layerSizes)
        self.a = [0] * len(self.layerSizes)
        self.a[0] = X

        for i in range(1, len(self.layerSizes)):
            self.z[i] = np.dot(self.a[i - 1], self.W[i - 1])
            self.a[i] = self.act(self.z[i])

        yHat = self.a[-1]
        return yHat

    def act(self, z):
        if self.useAct == "sigmoid":
            return self.sigmoid(z)
        elif self.useAct == "relu":
            return self.relu(z)
        elif self.useAct == "softRelu":
            return self.softRelu(z)
        else:
            return self.sigmoid(z)

    def actPrime(self, z):
        if self.useAct == "sigmoid":
            return self.sigmoidPrime(z)
        elif self.useAct == "relu":
            return self.reluPrime(z)
        elif self.useAct == "softRelu":
            return self.softReluPrime(z)
        else:
            return self.sigmoidPrime(z)

    def sigmoid(self, z):
        return 1 / (1 + np.exp(-z))

    def sigmoidPrime(self, z):
        return np.exp(-z) / ((1 + np.exp(-z)) ** 2)

    def relu(self, z):
        return np.fmax(0, z)

    def reluPrime(self, z):
        return np.where(z > 0, 1, 0)

    def softRelu(self, z):
        return np.log(1 + np.exp(z))

    def softReluPrime(self, z):
        return 1 / (1 + np.exp(-z))

    def costFunction(self, X, y):
        self.yHat = self.forward(X)
        J = 0.5 * sum((y - self.yHat) ** 2) / X.shape[0]
        reg = 0
        for W in self.W:
            reg = reg + np.sum(W ** 2)
        reg = (self.lambd / 2) * reg
        J = J + reg
        return J

    def costFunctionPrime(self, X, y):

        self.yHat = self.forward(X)

        delta = [0] * len(self.layerSizes)
        delta[-1] = np.multiply(-(y - self.yHat), self.actPrime(self.z[-1]))
        for i in reversed(range(1, len(self.W))):
            delta[i] = np.multiply(np.dot(delta[i + 1], self.W[i].T), self.actPrime(self.z[i]))

        dJdW = [0] * len(self.W)
        for i in reversed(range(0, len(self.W))):
            dJdW[i] = np.dot(self.a[i].T, delta[i + 1]) / X.shape[0] + self.lambd * self.W[i]

        return tuple(dJdW)

    def getParams(self):
        # Get weights unrolled into vector:
        params = np.empty(0)
        for W in self.W:
            params = np.concatenate((params, W.ravel()))
        return params

    def setParams(self, params):
        # print params
        # Set weights using single paramater vector.
        start = 0
        end = 0
        for i in range(0, len(self.W)):
            end = end + self.layerSizes[i] * self.layerSizes[i + 1]
            self.W[i] = np.reshape(params[start:end], (self.layerSizes[i], self.layerSizes[i + 1]))
            start = end

    def computeGradients(self, X, y):
        dJdW = self.costFunctionPrime(X, y)
        grad = np.empty(0)
        for dJ in dJdW:
            grad = np.concatenate((grad, dJ.ravel()))
        return grad


class SnakeCharmer(object):
    def __init__(self, N):
        # Make Local reference to network:
        self.N = N

    def callbackF(self, params):
        self.N.setParams(params)
        self.J.append(self.N.costFunction(self.X, self.y))

    def costFunctionWrapper(self, params, X, y):
        self.N.setParams(params)
        cost = self.N.costFunction(X, y)
        grad = self.N.computeGradients(X, y)

        return cost, grad

    def train(self, X, y):
        # Make an internal variable for the callback function:
        self.X = X
        self.y = y

        # Make empty list to store costs:
        self.J = []

        params0 = self.N.getParams()

        options = {'maxiter': 1000, 'disp': True}
        _res = optimize.minimize(self.costFunctionWrapper, params0, jac=True, method='BFGS', \
                                 args=(X, y), options=options, callback=self.callbackF)

        self.N.setParams(_res.x)
        self.optimizationResults = _res