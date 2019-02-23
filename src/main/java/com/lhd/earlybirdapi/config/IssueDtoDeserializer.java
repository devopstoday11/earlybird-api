package com.lhd.earlybirdapi.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.lhd.earlybirdapi.githubrepo.IssueDto;
import java.io.IOException;
import java.time.Instant;

public class IssueDtoDeserializer extends StdDeserializer<IssueDto> {

  public IssueDtoDeserializer() {
    this(null);
  }

  private IssueDtoDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public IssueDto deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    String createdAtString = node.get("created_at").asText();
    Instant createdAtInstant = Instant.parse(createdAtString);
    String htmlUrl = node.get("html_url").asText();
    return new IssueDto(createdAtInstant, htmlUrl);
  }
}
