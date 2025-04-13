import task0

with open('schedule.json', 'r', encoding='utf-8') as file:
    json = task0.parse_json(file)

with open('schedule.toml', 'w', encoding='utf-8') as toml:
    def parse_key_value(key, value, prefix=""):
        full_key = f'{prefix}.{key}' if prefix != "" else key
        if isinstance(value, dict):
            for k, v in value.items():
                parse_key_value(k, v, prefix=key)
        elif isinstance(value, list):
            for item in value:
                toml.write(f'\n[[{full_key}]]\n')
                parse_key_value("", item, prefix="")
                '''
                "" станет full_key, а потом префиксом из-за 11 строки, иначе будет чет типо wednesday.number_lesson = 3
                '''
        else:
            if isinstance(value, str):
                toml.write(f'{full_key} = "{value}"\n')
            elif isinstance(value, bool):
                toml.write(f'{full_key} = {str(value).lower()}\n')
            else:
                toml.write(f'{full_key} = {value}\n')


    for key, value in json.items():
        parse_key_value(key, value)
