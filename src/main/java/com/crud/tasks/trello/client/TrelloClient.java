package com.crud.tasks.trello.client;

import com.crud.tasks.domain.CreateTrelloCard;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCardDto;
import com.crud.tasks.trello.config.TrelloConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.*;

@Component
@RequiredArgsConstructor
public class TrelloClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrelloClient.class);
    private final RestTemplate restTemplate;
    private final TrelloConfig trelloConfig;

    private URI uriBuild() {
        try {
            return UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/members/" +
                            trelloConfig.getTrelloUserName() + "/boards")
                    .queryParam("key", trelloConfig.getTrelloAppKey())
                    .queryParam("token", trelloConfig.getTrelloToken())
                    .queryParam("fields", "name,id")
                    .queryParam("lists", "all")
                    .build()
                    .encode()
                    .toUri();
        } catch (Exception err)  {
            throw new IllegalArgumentException(err.getMessage(),err);
        }
    }

    public List<TrelloBoardDto> getTrelloBoards() {

        try {
            TrelloBoardDto[] boardsResponse = restTemplate.getForObject(uriBuild(), TrelloBoardDto[].class);

            List<TrelloBoardDto> trelloBoardDtoList = Optional.ofNullable(boardsResponse)
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList());

            return trelloBoardDtoList.stream()
                    .filter(name -> name.getName() != null)
                    .filter(id -> id.getId() != null)
               //     .filter(name -> name.getName().contains("Kodilla"))
                    .toList();
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public CreateTrelloCard createNewCard(TrelloCardDto trelloCardDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/cards")
                .queryParam("key", trelloConfig.getTrelloAppKey())
                .queryParam("token", trelloConfig.getTrelloToken())
                .queryParam("name", trelloCardDto.getName())
                .queryParam("desc", trelloCardDto.getDescription())
                .queryParam("pos", trelloCardDto.getPos())
                .queryParam("idList", trelloCardDto.getListId())
                .build()
                .encode()
                .toUri();

        return restTemplate.postForObject(url, null, CreateTrelloCard.class);
    }

//    private URI uriBuildForCardGet() {
//        try {
//            return UriComponentsBuilder.fromHttpUrl(trelloApiEndpoint + "/boards/636cc8f3d867bd0029c52a00/cards" )
//                    .queryParam("key", trelloAppKey)
//                    .queryParam("token", trelloToken)
//                    .build()
//                    .encode()
//                    .toUri();
//        } catch (Exception err)  {
//            throw new IllegalArgumentException(err.getMessage(),err);
//        }
//    }
//
//    public List<TrelloCardDto> getTrelloCard() {
//        TrelloCardDto[] cardsResponse = restTemplate.getForObject(uriBuildForCardGet(), TrelloCardDto[].class);
//              return Optional.ofNullable(cardsResponse)
//                      .map(Arrays::asList)
//                      .orElse(Collections.emptyList());
//    }

}
