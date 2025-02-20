package projetolivros.livros.Repository.Specs;


import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import projetolivros.livros.Model.Autor;
import projetolivros.livros.Model.Enum.GeneroLivro;
import projetolivros.livros.Model.Livro;

// ESTUDO SOBRE SPECIFICATION É POSSIVEL FAZER AS CONSULTAS COM @QUERY !
public class LivroSpecs {

    public static Specification<Livro> isbnEqual(String isbn){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isbn"), isbn));

    }
    public static Specification<Livro> tituloLike(String titulo) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.upper(root.get("titulo")), "%" + titulo.toUpperCase() + "%");
    }

    public static Specification<Livro> generoEqual(GeneroLivro genero){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("genero"), genero);
    }
    public static Specification<Livro> anoPulicacaoAno(Integer anoPulicacao){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.function
                        ("to_char",String.class,root.get("dataPublicacao"),
                                criteriaBuilder.literal("YYYY")), anoPulicacao.toString());
    }

    public static Specification<Livro> nomeEditoraLike(String nome){
        return (root, query, criteriaBuilder) -> {
            Join<Object,Object> joineditora = root.join("editora", JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.upper(joineditora.get("nome")), "%" + nome.toUpperCase() + "%");
        };
    }
    public static Specification<Livro> nomeAutorLike(String nome) {
        return (root, query, criteriaBuilder) -> {

            Join<Object, Object> joinautor = root.join("autor", JoinType.LEFT);

            return criteriaBuilder.like(
                    criteriaBuilder.upper(joinautor.get("nome")),
                    "%" + nome.toUpperCase() + "%"
            );
        };
    }
    public static Specification<Livro> precoBetween(Double precoMinimo, Double precoMaximo) {
        return (root, query, criteriaBuilder) -> {
            if (precoMinimo == null && precoMaximo == null) {
                return criteriaBuilder.conjunction(); // Retorna todos os livros se não houver filtro de preço
            } else if (precoMinimo == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("preco"), precoMaximo); // Filtra apenas pelo preço máximo
            } else if (precoMaximo == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("preco"), precoMinimo); // Filtra apenas pelo preço mínimo
            } else {
                return criteriaBuilder.between(root.get("preco"), precoMinimo, precoMaximo); // Filtra pelo intervalo de preço
            }
        };
    }
}
