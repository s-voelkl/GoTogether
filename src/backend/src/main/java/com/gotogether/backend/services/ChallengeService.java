package com.gotogether.backend.services;

import com.gotogether.backend.dto.ChallengeDTO;
import com.gotogether.backend.dto.ChallengeVerificationDTO;
import com.gotogether.backend.mapper.ChallengeMapper;
import com.gotogether.backend.model.Address;
import com.gotogether.backend.model.Company;
import com.gotogether.backend.model.Location;
import com.gotogether.backend.repository.ChallengeRepository;

import lombok.RequiredArgsConstructor;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository repo;

    private final ChallengeMapper challengeMapper;

    public ChallengeDTO getChallengeById(UUID id) {
        return repo.findById(id)
                .map(challengeMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
    }

    // public List<ChallengeDTO> getAllChallenges() {
    // return repo.findAll().stream()
    // .map(challengeMapper::toDTO)
    // .toList();
    // }

    public List<ChallengeDTO> getChallengesByFilter(/* filters */) {
        // filter options:
        // POST api/challenges 
        // body: 
        // { 
        // "interests": [], 
        // "socialBattery": 100,

        // “location” + radius?,
        // ... 
        // }

        // Applied verschiedene Filter, wenn vorhanden. 
        // Wenn z.B. Social Battery leer ist, soll dieses Feld als Filter nicht
        // berücksichtigt werden. 
        // Wenn interests leer ist ("[]"), soll das Feld nicht berücksichtigt werden
        // (anstatt einfach keins zurückzugeben).

        // Location auch nötig von Nutzer, mit Umkreis.
        return repo.findAll().stream()
                .map(challengeMapper::toDTO)
                .toList();
    }

    public ChallengeVerificationDTO verifyChallenge(UUID id, String verificationCode) {
        var challenge = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        if (challenge.getVerificationCode().equals(verificationCode)) {
            return challengeMapper.toVerificationDTO(challenge);
        } else {
            throw new RuntimeException("Invalid verification code");
        }
    }

}