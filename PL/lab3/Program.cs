public class Program
{
    private static readonly Cookbook cookbook = new Cookbook();

    public static void Main(string[] args)
    {
        while (true)
        {
            show(); 
            Console.Write("Выберите пункт меню: ");

            string? choice = Console.ReadLine();

            switch (choice)
            {
                case "1":
                    HandleAdd();
                    break;
                case "2":
                    HandleDel();
                    break;
                case "3":
                    HandleSearch();
                    break;
                case "4":
                    HandleExit();
                    return; 
                default:
                    Console.WriteLine("Неверный ввод. Пожалуйста, выберите пункт от 1 до 4.");
                    break;
            }

            Console.WriteLine("\nНажмите любую клавишу для возврата в меню...");
            Console.ReadKey();
        }
    }
    private static void show()
    {
        Console.Clear();
        Console.WriteLine("Опции:");
        Console.WriteLine("1. Добавить новый рецепт");
        Console.WriteLine("2. Удалить рецепт по названию");
        Console.WriteLine("3. Найти рецепт");
        Console.WriteLine("4. Сохранить и выйти");
        Console.WriteLine("");
    }

    private static void HandleAdd()
    {
        Console.Clear();
        Console.WriteLine("Добавление нового рецепта");
        
        Console.Write("Введите название: ");
        string title = Console.ReadLine() ?? "";
        if (string.IsNullOrWhiteSpace(title))
        {
            Console.WriteLine("Название не может быть пустым, подумайте и повторите");
            return;
        }

        Console.Write("Введите описание: ");
        string description = Console.ReadLine() ?? "";

        Console.Write("введите алгоритм приготовления: ");
        string steps = Console.ReadLine() ?? "";
        
        var newRecipe = new Recipe { Title = title, Description = description, Steps = steps };
        
        if (cookbook.AddRecipe(newRecipe))
        {
            Console.WriteLine("\nРецепт добавлен");
        }
        else
        {
            Console.WriteLine("\n Рецепт с таким названием уже существует");
        }
    }
    private static void HandleDel()
    {
        Console.Clear();
        Console.WriteLine("Удаление рецепта");

        Console.Write("Введите название для удаления: ");
        string title = Console.ReadLine() ?? "";

        if (cookbook.DeleteRecipe(title))
        {
            Console.WriteLine($"\nРецепт '{title}' удален");
        }
        else
        {
            Console.WriteLine($"\nРецепт с таким именем не найден");
        }
    }

    private static void HandleSearch()
    {
        Console.Clear();
        Console.WriteLine("Поиск рецепта");
        
        Console.Write("Введите слово для поиска: ");
        string query = Console.ReadLine() ?? "";
        if (string.IsNullOrWhiteSpace(query))
        {
            Console.WriteLine("Введите что-нибудь для поиска");
            return;
        }

        List<Recipe> results = cookbook.SearchRecipes(query);
        
        Console.WriteLine($"\nРезультаты поиска: '{query}'");

        if (results.Any())
        {
            foreach (var recipe in results)
            {
                Console.WriteLine(recipe);
                Console.WriteLine("--------------------");
            }
        }
        else
        {
            Console.WriteLine("По вашему запросу ничего не найдено");
        }
    }
    private static void HandleExit()
    {
        Console.WriteLine("\nСохранение книги");
        cookbook.Save();
        Console.WriteLine("Успех");
    }
}