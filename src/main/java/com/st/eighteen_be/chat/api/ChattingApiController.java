package com.st.eighteen_be.chat.api;

import com.st.eighteen_be.chat.constant.KafkaConst;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.dto.request.EnterChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.request.FindChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.service.facade.ChatroomFacade;
import com.st.eighteen_be.chat.service.impl.ChatroomService;
import com.st.eighteen_be.chat.service.kafka.ChattingProducer;
import com.st.eighteen_be.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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
    
    @MessageMapping("/chat/all")
    public ApiResponse<List<Object>> findAllMyChatrooms(@Valid @RequestBody FindChatRoomRequestDTO requestDTO) {
        log.info("findAllMyChatrooms.requestDTO.senderNo() = {}", requestDTO.senderNo());
        
        return ApiResponse.success(HttpStatus.OK, chatroomService.findAllMyChatrooms(requestDTO));
    }
    
    @MessageMapping("/chat/enter") // /pub/chat/enter
    public ApiResponse<List<ChatMessageResponseDTO>> enterChatroom(@Valid @RequestBody EnterChatRoomRequestDTO requestDTO) {
        log.info("enterChatroom.requestDTO.senderNo() = {}", requestDTO.senderNo());
        log.info("enterChatroom.requestDTO.receiverNo() = {}", requestDTO.receiverNo());
        log.info("enterChatroom.requestDTO.requestTime() = {}", requestDTO.requestTime());
        
        return ApiResponse.success(HttpStatus.OK, chatroomFacade.getOrCreateChatroom(requestDTO));
    }
    
    @MessageMapping("/chat/{senderNo}/{receiverNo}/message") // /pub/chat/{chatroomId}/message
    public void sendMessage(@DestinationVariable(value = "senderNo") Long senderNo, @DestinationVariable(value = "receiverNo") Long receiverNo, @RequestBody ChatMessageRequestDTO chatMessage) {
        log.info("sendMessage.senderNo() = {}, receiverNo() = {}", senderNo, receiverNo);
        log.info("sendMessage.chatMessage.message() = {}", chatMessage.message());
        
        chattingProducer.send(KafkaConst.CHAT_TOPIC, chatMessage);
    }
}