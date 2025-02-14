package projetolivros.livros.Service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projetolivros.livros.Dto.RegisterRequestDTO;

import projetolivros.livros.Exceptions.RegistroException;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.UsuarioRepository;
import projetolivros.livros.Utill.RandomString;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository userRepository;
    private final MailService mailService;

    public RegisterRequestDTO registerUser(Usuario user) throws UnsupportedEncodingException, MessagingException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RegistroException("Este e-mail já está cadastrado.");
        } else {

            String randomCode = RandomString.generateRandomString(64);
            user.setVerificationCode(randomCode);
            user.setEnabled(false);

            Usuario savedUser = userRepository.save(user);

            RegisterRequestDTO userResponse = new RegisterRequestDTO(
                    savedUser.getNome(),
                    savedUser.getEmail(),
                    savedUser.getPassword());

            try {
                mailService.sendVerificationEmail(user);
            } catch (MessagingException | UnsupportedEncodingException e) {
                // Você pode lançar uma exceção personalizada aqui se quiser tratar o erro de e-mail
                throw new RuntimeException("Erro ao enviar o e-mail de verificação", e);
            }

            return userResponse;
        }
    }

    public Usuario obterPorEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public boolean verify(String verificationCode){

        Usuario user = userRepository.findByVerificationCode(verificationCode);

        if(user == null || user.isEnabled()){
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);

            return true;
        }
    }

}
