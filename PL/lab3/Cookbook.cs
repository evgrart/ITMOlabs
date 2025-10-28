using System.Text;
using System.Text.Encodings.Web;
using System.Text.Json;
using System.Text.Unicode;

public class Cookbook
{
    private const string file = "cookbook.txt";
    private SortedDictionary<string, Recipe> _recipes;

    public Cookbook()
    {
        _recipes = new SortedDictionary<string, Recipe>(StringComparer.OrdinalIgnoreCase);
        Load();
    }


    public bool AddRecipe(Recipe recipe)
    {
        return _recipes.TryAdd(recipe.Title, recipe);
    }

    public bool DeleteRecipe(string title)
    {
        return _recipes.Remove(title);
    }

    public List<Recipe> SearchRecipes(string query)
    {
        var searchResults = _recipes.Values
            .Where(recipe =>
                recipe.Title.Contains(query, StringComparison.OrdinalIgnoreCase) ||
                recipe.Description.Contains(query, StringComparison.OrdinalIgnoreCase) ||
                recipe.Steps.Contains(query, StringComparison.OrdinalIgnoreCase)
            )
            .ToList();

        return searchResults;
    }

    public void Save()
    {
        try
        {
            var recipes = _recipes.Values.ToList();
            
            var options = new JsonSerializerOptions 
            { 
                WriteIndented = true,
                Encoder = JavaScriptEncoder.Create(UnicodeRanges.All)
            };
            
            string jsonString = JsonSerializer.Serialize(recipes, options);
            
            File.WriteAllText(file, jsonString, Encoding.UTF8);
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Ошибка при сохранении файла: {ex.Message}");
        }
    }
    
    private void Load()
    {
        if (!File.Exists(file))
        {
            return;
        }

        try
        {
            string jsonString = File.ReadAllText(file, Encoding.UTF8);

            if (string.IsNullOrWhiteSpace(jsonString))
            {
                return;
            }

            var loadedRecipes = JsonSerializer.Deserialize<List<Recipe>>(jsonString);

            _recipes.Clear();
            if (loadedRecipes != null)
            {
                foreach (var recipe in loadedRecipes)
                {
                    _recipes.TryAdd(recipe.Title, recipe);
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Какая-то ошибка: {ex.Message}");
            _recipes = new SortedDictionary<string, Recipe>(StringComparer.OrdinalIgnoreCase);
        }
    }

} 