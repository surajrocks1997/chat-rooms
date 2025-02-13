package com.chat_rooms.auth_handler.service;

import com.chat_rooms.auth_handler.dto.AppUser;
import com.chat_rooms.auth_handler.dto.GoogleUserInfo;
import com.chat_rooms.auth_handler.entity.AuthProvider;
import com.chat_rooms.auth_handler.entity.UserInfo;
import com.chat_rooms.auth_handler.global.CustomException;
import com.chat_rooms.auth_handler.repository.UserInfoRepository;
import com.chat_rooms.auth_handler.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordUtils passwordUtils;

    public Long saveGoogleUserToDb(GoogleUserInfo googleUserInfo) {
        log.info("saveGoogleUserToDb flow started");
        UserInfo user = UserInfo.builder()
                .firstName(googleUserInfo.getGivenName())
                .lastName(googleUserInfo.getFamilyName())
                .email(googleUserInfo.getEmail())
                .authProvider(AuthProvider.GOOGLE.getValue())
                .isEmailVerified(googleUserInfo.isEmailVerified())
                .isSocialLogin(true)
                .profilePictureUrl(googleUserInfo.getProfilePictureUrl())
                .build();

        UserInfo savedUser = userInfoRepository.save(user);

        log.info("saveGoogleUserToDb flow ended");
        return savedUser.getId();
    }

    public Long saveAppUserToDb(AppUser appUser) throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("saveAppUserToDb flow started");

        String[] name = appUser.getName().trim().split(" ");
        StringBuilder lastName = new StringBuilder();
        for (int i = 1; i < name.length; i++) {
            lastName.append(name[i]).append(" ");
        }

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        String hashedPassword = getHashedPassword(appUser.getPassword(), salt);

        UserInfo user = UserInfo.builder()
                .firstName(name[0])
                .lastName(lastName.toString())
                .email(appUser.getEmail())
                .password(hashedPassword)
                .salt(passwordUtils.encodeBase64(salt))
                .authProvider(AuthProvider.LOCAL.getValue())
                .isEmailVerified(true)
                .isSocialLogin(false)
                .build();


        UserInfo savedUser = userInfoRepository.save(user);
        log.info("saveAppUserToDb flow ended");
        return savedUser.getId();
    }


    private String getHashedPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return passwordUtils.encodeBase64(hash);
    }

    public boolean doesUserExist(String email) {
        UserInfo user = findUserByEmail(email);
        return user != null;
    }

    public UserInfo findUserByEmail(String email) {
        return userInfoRepository.findByEmail(email);
    }

    public UserInfo findUserById(Long id) {
        Optional<UserInfo> user = userInfoRepository.findById(id);
        if (user.isEmpty()) throw new CustomException("No User Present with Given Id : " + id, HttpStatus.BAD_REQUEST);
        return user.get();
    }

    public long validateAndRegister(AppUser appUser) throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("validateAndRegister flow started");
        boolean userExists = doesUserExist(appUser.getEmail());
        if (userExists) throw new CustomException("User Already Exists", HttpStatus.CONFLICT);

        log.info("validateAndRegister flow ended");
        return saveAppUserToDb(appUser);
    }

    public long validateLoginUser(AppUser appUser) throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.info("validateLoginUser flow started");

        UserInfo user = findUserByEmail(appUser.getEmail());

        if (user == null || !getHashedPassword(appUser.getPassword(), passwordUtils.decodeBase64(user.getSalt())).equals(user.getPassword()))
            throw new CustomException("Invalid Credentials", HttpStatus.BAD_REQUEST);

        log.info("validateLoginUser flow ended");
        return user.getId();
    }
}

