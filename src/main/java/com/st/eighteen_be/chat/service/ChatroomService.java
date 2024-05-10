package com.st.eighteen_be.chat.service;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.FindChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatroomWithLastestMessageDTO;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.mongo.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.chat.service.helper.ChatUserHelper;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatroomService {

    private final ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;

    @Transactional(readOnly = false)
    public ChatroomInfoCollection createChatroom(@NonNull final Long senderNo, @NonNull final Long receiverNo) {
        log.info("========== createChatroom ========== senderNo : {}, receiverNo : {}", senderNo, receiverNo);

        ChatUserHelper.validNotSameUser(senderNo, receiverNo);

        ChatroomInfoCollection newChatroom = ChatroomInfoCollection.of(senderNo, receiverNo, ChatroomType.PRIVATE);
        return chatroomInfoCollectionRepository.save(newChatroom);
    }

    public Optional<ChatroomInfoCollection> getChatroom(@NonNull final Long senderNo, @NonNull final Long receiverNo) {
        log.info("========== getChatroom ========== senderNo : {}, receiverNo : {}", senderNo, receiverNo);

        ChatUserHelper.validNotSameUser(senderNo, receiverNo);

        return chatroomInfoCollectionRepository.findBySenderNoAndReceiverNo(senderNo, receiverNo);
    }

    public List<ChatroomWithLastestMessageDTO> findAllMyChatrooms(@Valid FindChatRoomRequestDTO requestDTO) {
        log.info("========== findAllMyChatrooms ========== senderNo : {}", requestDTO.senderNo());

        return chatroomInfoCollectionRepository.findAllChatroomBySenderNo(requestDTO.senderNo());
    }
}