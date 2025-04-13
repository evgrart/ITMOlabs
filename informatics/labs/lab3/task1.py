import re


# number 422

def task1(line):
    count = len(re.findall(r'=-{P', line))
    return f'Number of smiles: {count}'

print(task1('=-P'))  # 0
print(task1('=-{--P'))  # 0
print(task1('=-{'))  # 0
print(task1('=-{P=-{p=-{P'))  # 2
print(task1('afjgsg3522=-{P45 vfv=-{vvPvvvdv     =-{P44'))  # 2
