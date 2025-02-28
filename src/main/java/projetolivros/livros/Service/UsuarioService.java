package projetolivros.livros.Service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projetolivros.livros.Dto.*;

import projetolivros.livros.Exceptions.RegistroException;
import projetolivros.livros.Model.Enum.UsuarioRole;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.UsuarioRepository;
import projetolivros.livros.Security.AuthenticatedUserProvider;
import projetolivros.livros.Utill.RandomString;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository userRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public ResponseDTO registerUserADMIN(RegisterRequestAdminDto body) throws UnsupportedEncodingException, MessagingException {

        Optional<Usuario> existingUser = Optional.ofNullable(userRepository.findByEmail(body.email()));
        if (existingUser.isPresent()) {
            throw new RegistroException("Este e-mail já está cadastrado.");
        }
        Usuario newUser = new Usuario();
        newUser.setEmail(body.email());
        newUser.setRole(UsuarioRole.ADMIN);
        newUser.setSenha(passwordEncoder.encode(body.senha()));
        newUser.setEnabled(true);
        userRepository.save(newUser);
        return new ResponseDTO(newUser.getNome(), null, newUser.getId());
    }
    public Usuario obterDados(){
        String email = authenticatedUserProvider.getAuthenticatedUsername();
        return userRepository.findByEmail(email);

    }

    public Usuario atualizarUsuario( UsuarioAtualizardto usuarioDTO) {
        String email = authenticatedUserProvider.getAuthenticatedUsername();
        Usuario usuario = userRepository.findByEmail(email);

        if (usuario != null) {
            usuario.setNome(usuarioDTO.getNome());
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setDataNascimento(usuarioDTO.getDataNascimento());
            if (usuario.getCpf() != null && !usuario.getCpf().isEmpty()) {
                usuarioDTO.setCpf(usuario.getCpf());
            }else{
            usuario.setCpf(usuarioDTO.getCpf());}

            return userRepository.save(usuario);
        }

        return null;
    }

    public ResponseDTO registerUser(RegisterRequestDTO body) throws UnsupportedEncodingException, MessagingException {


        Optional<Usuario> existingUser = Optional.ofNullable(userRepository.findByEmail(body.email()));
        if (existingUser.isPresent()) {
            throw new RegistroException("Este e-mail já está cadastrado.");
        }

        Usuario newUser = new Usuario();
        newUser.setNome(body.nome());
        newUser.setEmail(body.email());
        newUser.setRole(UsuarioRole.USER);
        newUser.setSenha(passwordEncoder.encode(body.senha()));
        String randomCode = RandomString.generateRandomString(64);
        newUser.setVerificationCode(randomCode);
        newUser.setEnabled(false);
        Usuario savedUser = userRepository.save(newUser);


        try {
            mailService.sendVerificationEmail(savedUser);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Erro ao enviar o e-mail de verificação", e);
        }


        return new ResponseDTO(savedUser.getNome(), null, savedUser.getId());
    }
    public Usuario obterPorEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public boolean verify(String verificationCode){

        Usuario user = userRepository.findByVerificationCode(verificationCode);

        if(user == null || user.isEnabled()){
            return false;
        }
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);
            return true;
    }
    public boolean alterarSenha( AlterarSenhadto alterarSenhaDTO) {
        String email = authenticatedUserProvider.getAuthenticatedUsername();
        Usuario usuario = userRepository.findByEmail(email);
            if (!passwordEncoder.matches(alterarSenhaDTO.getSenhaAtual(), usuario.getSenha())) {
                return false;
            }
            usuario.setSenha(passwordEncoder.encode(alterarSenhaDTO.getNovaSenha()));
            userRepository.save(usuario);
            return true;
    }


}
