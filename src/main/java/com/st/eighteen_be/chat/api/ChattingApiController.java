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
import com.st.eighteen_be.common.response.ApiResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@Tag(name = "ChattingApiController", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChattingApiController {
    private final ChattingProducer chattingProducer;
    private final ChatroomFacade chatroomFacade;
    private final ChatroomService chatroomService;
    
    @Operation(summary = "내 채팅방 조회", description = "내 채팅방을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    })
    @GetMapping("/api/v1/chat/all")
    public ApiResp<List<ChatroomWithLastestMessageDTO>> findAllMyChatrooms(@Valid @RequestBody FindChatRoomRequestDTO requestDTO) {
        log.info("findAllMyChatrooms.requestDTO.senderNo() = {}", requestDTO.senderNo());
        
        return ApiResp.success(HttpStatus.OK, chatroomService.findAllMyChatrooms(requestDTO));
    }
    
    @Operation(summary = "채팅방 입장", description = "채팅방에 입장하고, 채팅내역을 조회합니다. 없으면 생성하고 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "302", description = "INVALID REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    })
    @GetMapping("/api/v1/chat/enter")
    public ApiResp<List<ChatMessageResponseDTO>> enterChatroom(@Valid @RequestBody EnterChatRoomRequestDTO requestDTO) {
        log.info("enterChatroom.requestDTO.chatroomInfoId() = {}", requestDTO.chatroomInfoId());
        log.info("enterChatroom.requestDTO.requestTime() = {}", requestDTO.requestTime());
        
        return ApiResp.success(HttpStatus.OK, chatroomFacade.getOrCreateChatroom(requestDTO));
    }
    
    @Operation(summary = "채팅 메시지 전송", description = "채팅 메시지를 전송합니다.", ignoreJsonView = true)
    @MessageMapping("/v1/chat/{chatroom-id}/message") // /pub/chat/{senderNo}/{receiverNo}/message
    public void sendMessage(@DestinationVariable(value = "chatroom-id") String chatroomId, ChatMessageRequestDTO chatMessage) {
        log.info("sendMessage.chatMessage.senderNo() = {} , chatMessage.receiverNo() = {}", chatMessage.getSenderNo(), chatMessage.getReceiverNo());
        
        chattingProducer.send(KafkaConst.CHAT_TOPIC, chatMessage, chatroomId);
    }
}