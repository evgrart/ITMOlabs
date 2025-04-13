file1 = open("schedule.json", 'r', encoding='utf-8')
file2 = open('schedule.yaml', 'w', encoding='utf-8')


def parse_json(json):
    json = json.read().strip()
    if json.startswith('{'):
        return parse_object(json)
    elif json.startswith('['):
        return parse_list(json)
    else:
        raise ValueError("JSON должен начинаться с '{' или '['.")


def level(string, delimeter):  # делим строку по делиметру на высшем уровне вложенности
    result = []
    current = []
    depth = 0
    for char in string:
        if char in '{[':
            depth += 1
        elif char in '}]':
            depth -= 1
        elif char == delimeter and depth == 0:
            result.append(''.join(current).strip())
            current = []
            continue
        current.append(char)

    if current:
        result.append(''.join(current).strip())
    return result


def parse_object(string):
    obj = {}
    string = string[1:-1]
    pairs = level(string, ',')
    for pair in pairs:
        key, value = pair.split(':', 1)
        key = parse_value(key.strip())
        value = parse_value(value.strip())
        obj[key] = value
    return obj


def parse_list(string):
    string = string[1:-1]
    result = [parse_value(i) for i in level(string, ',')]
    return result


def parse_value(value):  # сериализуем примитив
    if value[0] == '"' and value[-1] == '"':
        return value[1:-1]
    elif value == "null":
        return None
    elif value == "true":
        return True
    elif value == "false":
        return False
    elif value.isdigit() or (value.startswith('-') and value[1:].isdigit()):
        return int(value)
    elif is_float(value):
        return float(value)
    elif value.startswith('{') and value.endswith('}'):
        return parse_object(value)
    elif value.startswith('[') and value.endswith(']'):
        return parse_list(value)
    else:
        raise ValueError(f"Неизвестное значение: {value}")


def is_float(num):
    try:
        float(num)
        return True
    except:
        return False


def to_yaml(json, count=0):
    for i in json.keys():
        if isinstance(json[i], dict):
            file2.write(f'{" " * count}{i}: \n')
            to_yaml(json[i], count + 2)
        elif isinstance(json[i], list):
            file2.write(f'{" " * count}{i}: \n')
            for j in json[i]:
                if isinstance(j, dict):
                    file2.write(f'{" " * (count + 2)}- \n')
                    to_yaml(j, count + 4)
                else:
                    file2.write(f'{" " * (count + 2)}- {j} \n')
        else:
            file2.write(f'{" " * count}{i}: {json[i]} \n')


json = parse_json(file1)
to_yaml(json)
file1.close()
file2.close()
