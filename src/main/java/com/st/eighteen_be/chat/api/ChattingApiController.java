package com.st.eighteen_be.chat.api;

import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.dto.request.EnterChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.request.FindChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatroomWithLastestMessageDTO;
import com.st.eighteen_be.chat.service.ChatMessageService;
import com.st.eighteen_be.chat.service.ChatroomService;
import com.st.eighteen_be.chat.service.facade.ChatroomFacade;
import com.st.eighteen_be.common.response.ApiResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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
@Tag(name = "채팅 API", description = "채팅 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChattingApiController {
    private final ChatroomFacade chatroomFacade;
    private final ChatroomService chatroomService;

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate smt;

    @Operation(summary = "내 채팅방 조회", description = "내 채팅방을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    })
    @GetMapping("/v1/api/chat/all/{sender-id}")
    public ApiResp<List<ChatroomWithLastestMessageDTO>> findAllMyChatrooms(
            @PathVariable("sender-id")
            @Parameter(description = "사용자 아이디", example = "qkrtkdwns3410", required = true)
            String senderId
    ) {
        log.info("findAllMyChatrooms.senderId() = {}", senderId);

        return ApiResp.success(HttpStatus.OK, chatroomService.findAllMyChatrooms(FindChatRoomRequestDTO.of(senderId)));
    }

    @Operation(summary = "채팅방 입장", description = "채팅방에 입장하고, 채팅내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "302", description = "INVALID REQUEST", content = @Content(schema = @Schema(implementation = ApiResp.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ApiResp.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
    })
    @GetMapping("/v1/api/chat/enter/{chatroomInfoId}")
    public ApiResp<List<ChatMessageResponseDTO>> enterChatroom(
            @PathVariable
            @Parameter(description = "채팅방 번호", example = "60f1b3b3b3b3b3b3b3b3b3b3" ,required = true)
            String chatroomInfoId,

            @Parameter(description = "요청 시간", example = "2021-04-12T00:00:00")
            @RequestParam(required = true)
            String requestTime
    ) {
        log.info("enterChatroom.chatroomInfoId() = {} , requestTime = {}", chatroomInfoId, requestTime);

        return ApiResp.success(HttpStatus.OK, chatroomFacade.getChatroom(EnterChatRoomRequestDTO.of(chatroomInfoId, requestTime)));
    }

    @Operation(summary = "채팅 메시지 전송", description = "채팅 메시지를 전송합니다.")
    @MessageMapping("/v1/api/chat/{chatroom-id}/message") // /pub/v1/api/chat/{chatroom-id}/message
    public void sendMessage(@DestinationVariable(value = "chatroom-id") String chatroomId, ChatMessageRequestDTO chatMessage) {
        log.info("sendMessage.chatMessage.senderNo() = {} , chatMessage.receiverNo() = {}", chatMessage.getSenderId(), chatMessage.getReceiverId());

        chatMessageService.send(chatMessage, chatroomId);
    }

    @Operation(summary = "채팅방 나가기", description = "채팅방에 나가기 시작합니다.")
    @PutMapping("/v1/api/chat/{chatroom-id}/quit")
    public ApiResp<Object> quitChatroom(
            @PathVariable("chatroom-id")
            @Parameter(description = "채팅방 번호", example = "60f1b3b3b3b3b3b3b3b3b3" ,required = true)
            String chatroomId,

            @Parameter(description = "나가는 사용자 아이디", example = "qkrtkdwns3410", required = true)
            @RequestParam(required = true)
            String quitUserId
    ) {
        chatroomFacade.quitChatroom(chatroomId, quitUserId);

        return ApiResp.success(HttpStatus.OK, "채팅방 나가기 성공");
    }
}
