using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Reflection.Emit;

namespace ExpressionEvaluator
{
        abstract class Node
    {
        public abstract string ExpressionText { get; }
        public abstract void GenerateIL(ILGenerator il, Dictionary<string, int> varMap, MethodInfo logMethod);
    }

    class ConstantNode : Node
    {
        public long Value { get; }
        public override string ExpressionText => Value.ToString();
        public ConstantNode(long value) => Value = value;
        public override void GenerateIL(ILGenerator il, Dictionary<string, int> varMap, MethodInfo logMethod)
        {
            il.Emit(OpCodes.Ldc_I8, Value);
        }
    }

    class VariableNode : Node
    {
        public string Name { get; }
        public override string ExpressionText => Name;
        public VariableNode(string name) => Name = name;
        public override void GenerateIL(ILGenerator il, Dictionary<string, int> varMap, MethodInfo logMethod)
        {
            if (varMap.TryGetValue(Name, out int index))
            {
                il.Emit(OpCodes.Ldarg_0); 
                il.Emit(OpCodes.Ldc_I4, index);
                il.Emit(OpCodes.Ldelem_I8);
            }
            else il.Emit(OpCodes.Ldc_I8, 0L);
        }
    }

    class BinaryOpNode : Node
    {
        public char Op { get; }
        public Node Left { get; }
        public Node Right { get; }
        public override string ExpressionText => $"{Left.ExpressionText}{Op}{Right.ExpressionText}";
        public BinaryOpNode(char op, Node left, Node right) { Op = op; Left = left; Right = right; }
        public override void GenerateIL(ILGenerator il, Dictionary<string, int> varMap, MethodInfo logMethod)
        {
            Left.GenerateIL(il, varMap, logMethod);
            Right.GenerateIL(il, varMap, logMethod);
            switch (Op)
            {
                case '+': il.Emit(OpCodes.Add); break;
                case '-': il.Emit(OpCodes.Sub); break;
                case '*': il.Emit(OpCodes.Mul); break;
                case '/': il.Emit(OpCodes.Div); break;
            }
            EmitLog(il, logMethod);
        }

        protected void EmitLog(ILGenerator il, MethodInfo logMethod)
        {
            var temp = il.DeclareLocal(typeof(long));
            il.Emit(OpCodes.Stloc, temp);
            il.Emit(OpCodes.Ldstr, ExpressionText);
            il.Emit(OpCodes.Ldloc, temp);
            il.Emit(OpCodes.Call, logMethod);
            il.Emit(OpCodes.Ldloc, temp);
        }
    }

    class ParenthesesNode : Node
    {
        public Node Inner { get; }
        public override string ExpressionText => $"({Inner.ExpressionText})";
        public ParenthesesNode(Node inner) => Inner = inner;

        public override void GenerateIL(ILGenerator il, Dictionary<string, int> varMap, MethodInfo logMethod)
        {
            Inner.GenerateIL(il, varMap, logMethod);
            var temp = il.DeclareLocal(typeof(long));
            il.Emit(OpCodes.Stloc, temp);
            il.Emit(OpCodes.Ldstr, ExpressionText);
            il.Emit(OpCodes.Ldloc, temp);
            il.Emit(OpCodes.Call, logMethod);
            il.Emit(OpCodes.Ldloc, temp);
        }
    }
    class Parser
    {
        private string input;
        private int pos = 0;
        public Parser(string s) => input = s.Replace(" ", "");
        private char Peek() => pos < input.Length ? input[pos] : '\0';
        private char Next() => pos < input.Length ? input[pos++] : '\0';
        public Node Parse() => ParseExpr();
        private Node ParseExpr()
        {
            var node = ParseTerm();
            while (Peek() == '+' || Peek() == '-')
            {
                char op = Next();
                node = new BinaryOpNode(op, node, ParseTerm());
            }
            return node;
        }

        private Node ParseTerm()
        {
            var node = ParseFactor();
            while (Peek() == '*' || Peek() == '/')
            {
                char op = Next();
                node = new BinaryOpNode(op, node, ParseFactor());
            }
            return node;
        }

        private Node ParseFactor()
        {
            if (Peek() == '(')
            {
                Next();
                var node = new ParenthesesNode(ParseExpr());
                Next();
                return node;
            }
            if (char.IsDigit(Peek()))
            {
                string s = "";
                while (char.IsDigit(Peek())) s += Next();
                return new ConstantNode(long.Parse(s));
            }
            if (char.IsLetter(Peek()))
            {
                string s = "";
                while (char.IsLetterOrDigit(Peek())) s += Next();
                return new VariableNode(s);
            }
            throw new Exception("Синтаксическая ошибка(");
        }
    }

    class Program
    {
        static Dictionary<string, long> vars = new Dictionary<string, long>();
        static List<string> varNames = new List<string>();
        static Delegate compiledEval = null;
        public static void LogIntermediate(string text, long val) => Console.WriteLine($"{text} = {val}");
        static void Main()
        {
            var logMethod = typeof(Program).GetMethod("LogIntermediate");
            while (true)
            {
                Console.Write("> ");
                string line = Console.ReadLine();
                if (string.IsNullOrWhiteSpace(line)) continue;
                var parts = line.Split(' ', 2, StringSplitOptions.RemoveEmptyEntries);
                string cmd = parts[0];
                try
                {
                    if (cmd == "expr" && parts.Length > 1)
                    {
                        var parser = new Parser(parts[1]);
                        var ast = parser.Parse();

                        var foundVars = new HashSet<string>();
                        void Collect(Node n)
                        {
                            if (n is VariableNode v) foundVars.Add(v.Name);
                            else if (n is BinaryOpNode b) { Collect(b.Left); Collect(b.Right); }
                            else if (n is ParenthesesNode p) Collect(p.Inner);
                        }
                        Collect(ast);
                        varNames = foundVars.ToList();
                        var varMap = varNames.Select((n, i) => new { n, i }).ToDictionary(x => x.n, x => x.i);
                        var dm = new DynamicMethod("Eval", typeof(long), new[] { typeof(long[]) }, typeof(Program));
                        var il = dm.GetILGenerator();
                        ast.GenerateIL(il, varMap, logMethod);
                        il.Emit(OpCodes.Ret);
                        compiledEval = dm.CreateDelegate(typeof(Func<long[], long>));
                    }
                    else if (cmd == "set" && parts.Length > 1)
                    {
                        var setParts = parts[1].Split(' ', StringSplitOptions.RemoveEmptyEntries);
                        vars[setParts[0]] = long.Parse(setParts[1]);
                    }
                    else if (cmd == "do")
                    {
                        if (compiledEval != null)
                        {
                            var values = varNames.Select(n => vars.ContainsKey(n) ? vars[n] : 0L).ToArray();
                            compiledEval.DynamicInvoke(new object[] { values });
                        }
                    }
                    else if (cmd == "exit") break;
                }
                catch (Exception ex) { Console.WriteLine("Ошибка: " + ex.Message); }
            }
        }
    }
}