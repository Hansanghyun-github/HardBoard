package com.example.HardBoard.api.service.inquiry;

import com.example.HardBoard.api.service.block.response.BlockResponse;
import com.example.HardBoard.api.service.inquiry.request.InquiryEditServiceRequest;
import com.example.HardBoard.api.service.inquiry.request.InquiryRegisterServiceRequest;
import com.example.HardBoard.api.service.inquiry.request.InquiryRespondServiceRequest;
import com.example.HardBoard.api.service.inquiry.response.InquiryResponse;
import com.example.HardBoard.domain.inquiry.Inquiry;
import com.example.HardBoard.domain.inquiry.InquiryRepository;
import com.example.HardBoard.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InquiryService {
    private final InquiryRepository inquiryRepository;

    public InquiryResponse registerInquiry(InquiryRegisterServiceRequest request) {
        Inquiry inquiry = inquiryRepository.save(
                Inquiry.builder()
                        .user(request.getUser())
                        .title(request.getTitle())
                        .contents(request.getContents())
                        .build()
        );
        return InquiryResponse.of(inquiry);
    }

    public void validateInquiry(Long inquiryId, User user) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid inquiryId"));
        if(inquiry.getUser().equals(user) == false)
            throw new IllegalArgumentException("Can't control other user's inquiry");
    }

    // TODO validate와 edit에서 모두 findById를 사용하는데, 다른 Transaction이라 1차 캐싱 안됨 -> 해결 가능?

    public InquiryResponse editInquiry(InquiryEditServiceRequest request) {
        Inquiry inquiry = inquiryRepository.findById(request.getInquiryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid inquiryId"));
        inquiry.edit(request.getTitle(), request.getContents());
        return InquiryResponse.of(inquiry);
    }

    public void deleteInquiry(Long inquiryId) {
        inquiryRepository.deleteById(inquiryId);
    }

    public InquiryResponse respondInquiry(InquiryRespondServiceRequest request, LocalDateTime respondDateTime) {
        Inquiry inquiry = inquiryRepository.findById(request.getInquiryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid inquiryId"));
        inquiry.respond(request.getResponse(), respondDateTime);
        return InquiryResponse.of(inquiry);
    }

    public List<InquiryResponse> getInquiryList(Long userId, int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 20,
                Sort.by(Sort.Direction.DESC, "createdDateTime"));
        return inquiryRepository.findByUserId(userId, pageRequest)
                .map(InquiryResponse::of)
                .getContent();
    }

    public InquiryResponse getInquiry(Long inquiryId) {
        return InquiryResponse.of(
                inquiryRepository.findById(inquiryId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid inquiryId")));
    }
}
