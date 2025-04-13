import json
import yaml


def json_to_yaml(json_file, yaml_file):
    with open(json_file, 'r', encoding='utf-8') as file:
        data = json.load(file)  # десериализует JSON-данные в Python-объекты

    with open(yaml_file, 'w', encoding='utf-8') as file:
        yaml.dump(data, file, default_flow_style=False, allow_unicode=True)
        """
        default_flow_style=False - вложенные структуры данных будут записываться с отступами и в читаемом виде, а не в компактном
        allow_unicode=True -  позволяет сохранять символы юникода в YAML
        """


json_file = 'schedule.json'
yaml_file = 'example.yaml'
json_to_yaml(json_file, yaml_file)
