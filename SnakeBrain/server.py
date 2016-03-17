import numpy as np
import neuralnetwork as nn
import config
from flask import Flask
from flask import request
from flask import jsonify
import csv

app = Flask(__name__)


# Initialize neural net

# Hyperparameters
inputLayerSize = config.inputLayerSize
outputLayerSize = config.outputLayerSize
hiddenLayerSize = config.hiddenLayerSize

# Regularization
lambd = config.lambd

# Configure activation function
useAct = config.useAct

net = nn.NeuralNetwork(inputLayerSize, outputLayerSize, hiddenLayerSize, lambd, useAct)

with open('../Parameters/active/weights{}.csv'.format(config.suffix), 'r') as f:
    reader = csv.reader(f)
    params = np.array(list(reader), dtype=float)

net.setParams(params)


# Route configurations

@app.route("/forward", methods=['POST'])
def forward():
    json = request.get_json()
    X = np.array(json['X'], dtype=float)
    yHat = net.forward(X)
    return jsonify({'yHat': yHat.tolist()})


if __name__ == "__main__":
    app.run(debug=True)
