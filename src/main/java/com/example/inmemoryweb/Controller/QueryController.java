package com.example.inmemoryweb.Controller;

import com.example.inmemoryweb.Service.QueryService;
import com.example.inmemoryweb.databasestructure.Query;
import com.example.inmemoryweb.databasestructure.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static com.example.inmemoryweb.Configuration.Constants.TABLE_DATA_VIEW_NAME;

@Controller
@RequestMapping("/query")
public class QueryController {

    private QueryService queryService;

    @PostMapping()
    private ModelAndView executeUserQuery(@ModelAttribute Query query) {
        ModelAndView modelAndView;
        modelAndView = getModelAndView(query);
        return modelAndView;
    }

    @GetMapping("/{tableName}")
    private ModelAndView getData(@PathVariable("tableName") String tableName) {
        Query query = new Query();
        query.setQueryBody("SELECT * FROM " + tableName);
        return getModelAndView(query);
    }

    private ModelAndView getModelAndView(Query query) {
        ModelAndView modelAndView;
        try {
            QueryResult queryResult = queryService.executeQuery(query);
            modelAndView = generateModelObject(queryResult);
        } catch (Exception e) {
            modelAndView = addErrorMessageToView(e.getMessage());
        }
        return modelAndView;
    }

    private ModelAndView generateModelObject(QueryResult queryResult) {
        if (queryResult == null) return new ModelAndView(TABLE_DATA_VIEW_NAME);

        ModelAndView modelAndView;
        boolean isThereResultInResponse = queryResult.getQueryResult() != null;
        if (isThereResultInResponse) {
            modelAndView = addQueryResultToView(queryResult);
        } else {
            modelAndView = viewTableData(queryResult.getTableName());
        }
        return modelAndView;
    }

    private ModelAndView addQueryResultToView(QueryResult queryResult) {
        ModelAndView modelAndView = new ModelAndView(TABLE_DATA_VIEW_NAME);
        modelAndView.addObject("queryResult", queryResult.getQueryResult());
        modelAndView.addObject("tableColumns", queryResult.getTableColumnsNames());
        modelAndView.addObject("tableName", queryResult.getTableName());
        return modelAndView;
    }

    private ModelAndView viewTableData(String tableName) {
        return new ModelAndView("redirect:/query/" + tableName);
    }

    private ModelAndView addErrorMessageToView(String errorMessage) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.setViewName(TABLE_DATA_VIEW_NAME);
        return modelAndView;
    }


    @Autowired
    public void setQueryService(QueryService queryService) {
        this.queryService = queryService;
    }
}


