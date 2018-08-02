#-*-coding:utf-8-*-
import csv
import json
import sys
import codecs
import numpy as np
import codecs
def trans(json_path, csv_path):
    json_file = codecs.open(json_path, 'r', 'utf-8')
    flag = True
    csvfile = open(csv_path, 'w', newline='')
    writer = csv.writer(csvfile)
    json_data = json.load(json_file)
    node_data = json_data['nodes']
    attributes = list(node_data[0].keys())
    attributes.remove('attributes')
    attributes.remove('color')
    #print(attributes)
    attributes, orderlist = orderDict(attributes)
    #print(attributes)
    #print(orderlist)
    extra_attributes = list(node_data[0]['attributes'].keys())
    for idx,ele in enumerate(extra_attributes):
        if ele == "度":
            extra_attributes[idx] = "Degree"
        if ele == "连入度":
            extra_attributes[idx] = "inDegree"
        if ele == "连出度":
            extra_attributes[idx] = "outDegree"
    attributes = attributes + extra_attributes
    #print(attributes)
    writer.writerow(attributes)
    for node in node_data:
        del node['color']
        attr = list(node['attributes'].values())
        del node['attributes']
        order_dict = orderValue(list(node.values()), orderlist)
        #print(order_dict)
        attr = order_dict + attr
        writer.writerow(attr)
    #
    csvfile.close()
def orderDict(list):
    new_list = ['id','label', 'x' , 'y']
    orderlist = []
    list = np.array(list)
    orderlist.append(np.where(list == 'id')[0][0])
    orderlist.append(np.where(list == 'label')[0][0])
    orderlist.append(np.where(list == 'x')[0][0])
    orderlist.append(np.where(list == 'y')[0][0])

    for idx,ele in enumerate(list):
        if ele in new_list :
            continue
        else:

            orderlist.append(idx)
            new_list.append(ele)
    return new_list, orderlist

def orderValue(list, orderlist):
    value = []
    for idx in orderlist:
        value.append(list[idx])
    return value





if __name__ == '__main__':
    # path=str(sys.argv[1]) # 获取path参数
    # print (path)
    json_path = sys.argv[1]
    csv_path = sys.argv[2]
    # json_path = "E:\\network\\network\\data.json"
    # csv_path = "E:\\network\\network\\data.csv"
    trans(json_path, csv_path)