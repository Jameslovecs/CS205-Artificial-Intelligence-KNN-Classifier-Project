import math
import random
#read file
def read_file(file_name = "Users/caoranran/Desktop/AI\ Project/cs_205_NN_datasets/cs_205_small58.txt 
"):
    file = open(file_name)
    line  = file.readline()
    data = []
    while line:
        line = line.strip()
        if line != "":
            data.append(line.split())
        line = file.readline()
    return data
#normalize data
def normalize(data):
    mean = [0] * len(data[0])
    std = [0] * len(data[0])
    norm_data = []
    #compute mean
    for i in range(len(data)):
        for j in range(len(data[0])):
            data[i][j] = float(data[i][j])
            mean[j] += data[i][j]
    for i in range(len(mean)):
        mean[i] /= len(data)
    #compute std
    for j in range(len(data[0])):
        temp = 0;
        for i in range(len(data)):
            if j == 0:
                continue;
            temp += math.pow(data[i][j] - mean[j], 2)
        std[j] = math.sqrt(temp / len(data))
    #normalize data
    for i in range(len(data)):
        list = []
        for j in range(len(data[0])):
            if j == 0:
                list.append(data[i][j])
                continue
            list.append((data[i][j] - mean[j]) / std[j])
        norm_data.append(list)
    return norm_data
#leave-one-out validation
def cross_validation(data, features):
    res = []
    for i in range (len(data)):
        nearest = nearest_neighbor(i, data, features)
        res.append(nearest[0])
    accuracy = compute_accuracy(data, res)
    return accuracy

#compute accuracy of classifier
def compute_accuracy(data, result):
    count = 0.0
    for i in range(len(result)):
        if result[i] == data[i][0]:
            count += 1
    return count / len(data)

#find the nearest neighbor of features for specified dataset
def nearest_neighbor(test, data, features):
    min_dis = float('inf')
    nearest = 0
    for i in range(len(data)):
        if i == test:
            continue
        dis = 0
        for j in features:
            dis += math.pow(data[i][j] - data[test][j], 2)
        dis = math.sqrt(dis)
        if dis < min_dis:
            min_dis = dis
            nearest = i
    return data[nearest]
#compute initial accuracy
def default_accuracy(data):
    count1 = 0.0
    count2 = 0.0
    for record in data:
        if record[0] == 1:
            count1 += 1
        else:
            count2 += 1
    return max(count1, count2) / len(data)
#forward selection
def forward_selection(data, features, accuracy, visited):
    if len(features) == len(data[0]) - 1:
        return
    max = 0.0
    new_features = []
    index = 0
    print accuracy
    print features
    for i in range(len(data[0])):
        if i == 0 or visited[i] == True:
            continue
        features.append(i)
        acc = cross_validation(data, features)
        #record max accuracy and features
        if acc > max:
            index = i
            max = acc
            new_features = list(features)
        features.remove(i)
    if max < accuracy:
        print "(Warning, Accuracy has decreased! Continuing search in case of local maxima)"
    visited[index] = True
    forward_selection(data, new_features, max, visited)
#backward elimination
def backward_selection(data, features, accuracy, visited):
    if len(features) == 0:
        return
    max = 0.0
    new_features = []
    index = 0
    print accuracy
    print features
    for i in range(len(data[0])):
        if i == 0 or visited[i] == True:
            continue
        features.remove(i)
        acc = cross_validation(data, features)
        #record max accuracy and feature deleted
        if acc > max:
            index = i
            max = acc
            new_features = list(features)
        features.append(i)
    if max < accuracy:
        print "(Warning, Accuracy has decreased! Continuing search in case of local maxima)"
    visited[index] = True
    backward_selection(data, new_features, max, visited)

#special selection using annealing algorithm
def annealing_selection(data, features, accuracy, visited):
    if len(features) == 0:
        return
    max = 0.0
    new_features = []
    index = 0
    print accuracy
    print features

    temp_acc = []
    temp_feat = []
    delete_feat = []

    for i in range(len(data[0])):
        if i == 0 or visited[i] == True:
            continue
        #record accuracy and left features everytime delete a feature
        features.remove(i)
        acc = cross_validation(data, features)
        temp_acc.append(acc)
        temp_feat.append(list(features))
        delete_feat.append(i)
        if acc > max:
            index = i
            max = acc
            new_features = list(features)
        features.append(i)
    #if there is no greedy selection, using random annealing selection
    if max < accuracy:
        rand = compute_probability(temp_feat, temp_acc, accuracy)
        if rand == None:
            print temp_feat
        visited[delete_feat[rand]] = True
        annealing_selection(data, list(temp_feat[rand]), temp_acc[rand], visited)
    else:
        visited[index] = True
        annealing_selection(data, new_features, max, visited)

def compute_probability(temp_feat, temp_acc, accuracy):
    if len(temp_feat) == 0:
        return
    #compute probability of a feature it will be deleted
    while True:
        rand = random.randint(0, len(temp_feat) - 1)
        # t is number of left features, delta-k is difference of accuracy
        pk = 1 / (1 + math.exp((temp_acc[rand] - accuracy)/len(temp_acc)))
        if random.random() < pk:
            print "No better selection, Annealing Selection"
            return rand


if __name__ == "__main__":
    print "Welcome to Feature Selection Algorithm:"
    filename = raw_input("Type in the path of dataset:")
    option = raw_input("\nChoose the algorithm you want to run.\n\n1.Forward Selection.\n2.Backward Selection.\n3.Simply Annealing Algorithm.\n")
    print "Reading and Normalizing data,please wait......\n"

    accuracy = []
    feature_set = []
    data = normalize(read_file("/Users/leo/Documents/KNN/cs_205_large20.txt"))
    default = default_accuracy(data)
    visited = [False] * len(data[0])

    features = []
    for i in range(len(data[0])):
        if i == 0:
            continue
        features.append(i)
    ini = cross_validation(data, features)

    print "Done."
    print "The database has",len(data[0]) - 1, "features and ", len(data), "instances"
    print "Runing nearest neighbors with all ", len(data[0]) - 1, '''features, using "leave one out" evaluation, I got an accuracy of ''', ini
    #option = "3"
    if option == "1":
        print "Forward Selection begins:"
        features = []
        forward_selection(data, features, 0, visited)
        print "Forward Selection ends:"
    elif option == "2":
        print "Backward Elimination begins:"
        backward_selection(data, features, 0, visited)
        print "backward Elimination ends:"
    elif option == "3":
        print "Annealing Algorithm begins:"
        annealing_selection(data, features, ini, visited)
        print "Annealing Algorithm ends:"
