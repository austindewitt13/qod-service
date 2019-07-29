package edu.cnm.deepdive.qod.controller;

import edu.cnm.deepdive.qod.SpringRestDocsApplication;
import edu.cnm.deepdive.qod.model.entity.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.swing.*;

import java.util.UUID;

import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(classes = SpringRestDocsApplication.class)
class QuoteControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(document("{method-name}",
                        preprocessRequest(Preprocessors.prettyPrint()), preprocessResponse(Preprocessors.prettyPrint())))
                .build();
    }

    @Test
    void getRandom() throws Exception {
        mockMvc.perform(get("/quotes/random"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("get-random",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
//                        links(linkWithRel("self").description("URL of this quote")),
                        responseHeaders(headerWithName("Content-Type")
                                .description("Content type of payload"))
                        )
                );

    }

    @Test
    void get404() throws Exception {
        mockMvc.perform(get("/quotes/01234567-89AB-CDEF-0123-456789ABCDEF"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void postQuote() throws Exception {
        mockMvc.perform
                (post("/quotes")
                        .content("{\"text\":\"Be excellent to each other.\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }
}