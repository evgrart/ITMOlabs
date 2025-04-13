import re


# number 4

def task2(line):
    result = re.sub(r'(?<![:а-яА-Я\d])([01]\d|2[0-3]):[0-5]\d(:[0-5]\d)?\b(?![:])', '(TBD)', line)
    return result

print(task2('В эту субботу в 15:00:23планируется доп. занятие на 2 часа.'))
print(task2("Class at 14:45 has been replaced."))  # Class at (TBD) has been replaced.
print(
    task2("Classes at 13:45 and at 15:30:22 have been replaced."))  # Classes at (TBD) and at (TBD) have been replaced.
print(task2("The start at 00:00 and the end at 23:59:59."))  # The start at (TBD) and the end at (TBD).
print(task2("Time 25:00 and 00:60."))  # Time 25:00 and 00:60.
print(task2("Time 213:100:00."))  # Time 213:100:00.

