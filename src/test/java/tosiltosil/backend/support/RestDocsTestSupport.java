package tosiltosil.backend.support;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tosiltosil.backend.module.goal.presentation.GoalController;
import tosiltosil.backend.support.RestDocsTestSupport.RestDocsTestConfig;

@WebMvcTest(GoalController.class)
@AutoConfigureRestDocs
@Import(RestDocsTestConfig.class)
@WithMockCustomUser
public abstract class RestDocsTestSupport {

    @Autowired
    protected MockMvcTester mockMvcTester;

    @Autowired
    protected RestDocumentationResultHandler documentHandler;

    protected static ParameterDescriptor pathParameter(
            final String name,
            final String description
    ) {
        return parameterWithName(name).description(description);
    }

    protected static ParameterDescriptor queryParameter(
            final String name,
            final String description
    ) {
        return parameterWithName(name).description(description);
    }

    protected static FieldDescriptor requestField(
            final String jsonPath,
            final JsonFieldType type,
            final String description,
            final boolean optional,
            final String constraint,
            final String example
    ) {
        FieldDescriptor fieldDescriptor = fieldWithPath(jsonPath)
                .type(type)
                .description(description)
                .optional()
                .attributes(key("constraint").value(constraint))
                .attributes(key("example").value(example));
        return optional ? fieldDescriptor.optional() : fieldDescriptor;
    }

    protected static FieldDescriptor responseField(
            final String jsonPath,
            final JsonFieldType type,
            final String description,
            final String example
    ) {
        return fieldWithPath(jsonPath)
                .type(type)
                .description(description)
                .attributes(key("example").value(example));
    }

    @TestConfiguration
    static class RestDocsTestConfig {

        @Bean
        public RestDocumentationResultHandler restDocumentationResultHandler() {
            return MockMvcRestDocumentation.document("{class-name}/{method-name}",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));
        }

        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(authorize -> authorize
                            .anyRequest().permitAll())
                    .build();
        }
    }
}
