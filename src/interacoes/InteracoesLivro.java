package interacoes;

import ArquivosUteis.CRUD;
import entities.Livro;
import java.io.File;
import java.util.Scanner;

public class InteracoesLivro {

  private static Scanner console = new Scanner(System.in);
  private CRUD<Livro> arqLivros;

  public InteracoesLivro() {
    try {
      File d;
      d = new File("dados");
      if (!d.exists())
        d.mkdir();
      d = new File("dados/livros");
      if (!d.exists())
        d.mkdir();
      arqLivros = new CRUD<>("dados/livros", Livro.class.getConstructor());
    } catch (Exception e) {
      System.out.println("Arquivo não pode ser aberto ou criado.");
      e.printStackTrace();
    }
  }

  public Livro leLivro() throws Exception {
    System.out.print("\nTítulo: ");
    String titulo = console.nextLine();
    System.out.print("\nAutor: ");
    String autor = console.nextLine();
    System.out.print("\nPreço: ");
    float preco = Float.parseFloat(console.nextLine());
    Livro l = new Livro(titulo, autor, preco);
    return l;
  }

  public void mostraLivro(Livro l) {
    System.out.println(
        "\nTítulo: " + l.getTitulo() +
            "\nAutor: " + l.getAutor() +
            "\nPreço: R$ " + l.getPreco());
  }

  public void menuLivros() {
    int opcao;
    do {
      System.out.println("\nMENU DE LIVROS");
      System.out.println("\n1) Incluir livro");
      System.out.println("2) Buscar livro");
      System.out.println("3) Alterar livro");
      System.out.println("4) Excluir livro");
      System.out.println("\n0) Retornar ao menu anterior");

      System.out.print("\nOpção: ");
      try {
        opcao = Integer.valueOf(console.nextLine());
      } catch (NumberFormatException e) {
        opcao = -1;
      }

      switch (opcao) {
        case 1:
          incluirLivro();
          break;
        case 2:
          buscarLivro();
          break;
        case 3:
          // alterarLivro();
          break;
        case 4:
          // excluirLivro();
          break;
        case 0:
          break;
        default:
          System.out.println("Opção inválida");
      }
    } while (opcao != 0);
  }

  public void incluirLivro() {
    Livro novoLivro;
    try {
      novoLivro = leLivro();
    } catch (Exception e) {
      System.out.println("Dados inválidos");
      return;
    }

    int id;
    try {
      id = arqLivros.create(novoLivro);
    } catch (Exception e) {
      System.out.println("Livro não pode ser criado");
      e.printStackTrace();
      return;
    }

    System.out.println("\nLivro criado com o ID " + id);

  }

  public void buscarLivro() {
    int id;
    System.out.print("\nID do Livro: ");
    try {
      id = Integer.valueOf(console.nextLine());
    } catch (NumberFormatException e) {
      System.out.println("ID inválido.");
      return;
    }

    try {
      Livro l = arqLivros.read(id);
      mostraLivro(l);
    } catch (Exception e) {
      System.out.println("Erro no acesso ao arquivo");
      e.printStackTrace();
    }

  }
	
}
