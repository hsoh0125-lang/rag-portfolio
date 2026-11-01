package com.example.backend.document;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentRepository documentRepository;

    @Test
    @DisplayName("정상 요청이면 201과 PENDING 상태로 만들어진다")
    void createDocument() throws Exception {
        given(documentRepository.save(any(Document.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fileName\":\"memo.pdf\",\"fileType\":\"pdf\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("memo.pdf"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("파일 이름이 비어 있으면 400")
    void blankFileName() throws Exception {
        mockMvc.perform(post("/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fileName\":\"\",\"fileType\":\"pdf\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("파일 이름 항목이 없으면 400")
    void missingFileName() throws Exception {
        mockMvc.perform(post("/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fileType\":\"pdf\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("허용하지 않는 파일 종류(exe)면 400")
    void invalidFileType() throws Exception {
        mockMvc.perform(post("/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fileName\":\"a.exe\",\"fileType\":\"exe\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("없는 번호를 조회하면 이유가 담긴 404")
    void getNotFound() throws Exception {
        given(documentRepository.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/documents/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("없는 번호를 지우면 404")
    void deleteNotFound() throws Exception {
        given(documentRepository.existsById(999L)).willReturn(false);

        mockMvc.perform(delete("/documents/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("있는 문서의 상태를 COMPLETED로 바꾸면 200")
    void changeStatus() throws Exception {
        Document document = new Document("memo.pdf", "pdf");
        given(documentRepository.findById(1L)).willReturn(Optional.of(document));
        given(documentRepository.save(any(Document.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(patch("/documents/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
