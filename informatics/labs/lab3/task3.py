import re


# number 2

def task3(line):
    res = re.split(r'([Цц]ена Bitcoin)([^\.]*)([$¥£€₽₣][\d,\.\s]+) (RUB|USD|EUR|GBP|JPY|CHF)', line)
    return [i for i in res if re.fullmatch(r'[$¥£€₽₣][\d,\.\s]+', i)][0][1:] if len(res) > 1 else 'No price'

print(task3("meta name=daily_volume content=В суточным объемом торгов ₽2,835,029,974,960.63 RUB./> <meta name=daily_price content=Мы обновляем нашу цену BTC к RUB в режиме реального времени./> <meta name=daily_price content= Цена Bitcoin в реальном времени сегодня составляет ₽5,797,806.88 RUB./><meta name=daily_price content=Ethereum стоит на данный момент ₽229,590,78 RUB./"))
print(task3(
    "<meta name='daily_volume' content='В суточным объемом торгов ₽2,835,029,974,960.63 RUB.'/> "
    "<meta name='daily_price' content='Мы обновляемнашу цену BTC к RUB в режиме реального времени.'/> <meta "
    "name='daily_price' content='Цена Bitcoin в реальном времени сегодня составляет ₽5,797,806.88 RUB.'/><meta "
    "name='daily_price' content='Ethereum стоит на данный момент ₽229,590,78 RUB.'/>"))  # 5,797,806.88

print(task3("<meta name='daily_price' content='Цена Bitcoin в "
            "реальном времени сегодня составляет ₽57978₽6,88 RUB.'/>"))  # 6,88

print(task3("<meta name='daily_price' content='Цена Ethereum "
            "на данный момент составляет ₽229,590.78 RUB.'/>"))  # No price

print(task3("В суточным объемом торгов цена ₽2,835,029,974,960.63 RUB."))  # No price
print(task3("В суточным объемом торгов цена Bitcoin $2,835,029.974,960.63 USD."))  # 2,835,029.974,960.63
