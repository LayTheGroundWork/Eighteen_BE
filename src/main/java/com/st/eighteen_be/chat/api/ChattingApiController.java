package com.st.eighteen_be.chat.api;

import com.st.eighteen_be.chat.constant.KafkaConst;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.dto.request.EnterChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.request.FindChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatroomWithLastestMessageDTO;
import com.st.eighteen_be.chat.service.ChatroomService;
import com.st.eighteen_be.chat.service.facade.ChatroomFacade;
import com.st.eighteen_be.chat.service.kafka.ChattingProducer;
import com.st.eighteen_be.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.chat.api
 * fileName       : ChattingApiController
 * author         : ipeac
 * date           : 2024-03-29
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-29        ipeac       최초 생성
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChattingApiController {
    private final ChattingProducer chattingProducer;
    private final ChatroomFacade chatroomFacade;
    private final ChatroomService chatroomService;
    
    @MessageMapping("/v1/chat/all")
    public ApiResponse<List<ChatroomWithLastestMessageDTO>> findAllMyChatrooms(@Valid @RequestBody FindChatRoomRequestDTO requestDTO) {
        log.info("findAllMyChatrooms.requestDTO.senderNo() = {}", requestDTO.senderNo());
        
        //TODO 읽지 않은 메시지에 대하여 별도 처리 필요합니다.
        
        return ApiResponse.success(HttpStatus.OK, chatroomService.findAllMyChatrooms(requestDTO));
    }
    
    @MessageMapping("/v1/chat/enter") // /pub/v1/chat/enter
    public ApiResponse<List<ChatMessageResponseDTO>> enterChatroom(@Valid @RequestBody EnterChatRoomRequestDTO requestDTO) {
        log.info("enterChatroom.requestDTO.senderNo() = {}", requestDTO.senderNo());
        log.info("enterChatroom.requestDTO.receiverNo() = {}", requestDTO.receiverNo());
        log.info("enterChatroom.requestDTO.requestTime() = {}", requestDTO.requestTime());
        
        return ApiResponse.success(HttpStatus.OK, chatroomFacade.getOrCreateChatroom(requestDTO));
    }
    
    @MessageMapping("/v1/chat/message") // /pub/v1/chat/message
    public void sendMessage(@RequestBody ChatMessageRequestDTO chatMessage) {
        log.info("sendMessage.chatMessage.message() = {}", chatMessage.getMessage());
        log.info("sendMessage.chatMessage.senderNo() = {}", chatMessage.getSenderNo());
        log.info("sendMessage.chatMessage.receiverNo() = {}", chatMessage.getReceiverNo());
        
        chattingProducer.send(KafkaConst.CHAT_TOPIC, chatMessage);
    }
}