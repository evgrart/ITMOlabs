public class Recipe
{
    public string Title { get; set; } = string.Empty;
    public string Description { get; set; } = string.Empty;
    public string Steps { get; set; } = string.Empty;

    public override string ToString()
    {
        return $"{Title} \n" +
               $"Описание: {Description}\n" +
               $"Шаги приготовления:\n{Steps}\n";
    }
}