// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.google.gson.Gson;
import java.util.List;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.Comment;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private ArrayList<Comment> comments;

  @Override
  public void init() {
    comments = new ArrayList<>();
  }


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
        long id = entity.getKey().getId();
        String name = (String) entity.getProperty("name");
        String message = (String) entity.getProperty("message");
        String score = (String) entity.getProperty("score");

        Comment com = new Comment(id, name, message, score);
        comments.add(com);
    }
    
    String json = convertToJsonUsingGson(comments);
    
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  private String convertToJsonUsingGson(ArrayList<Comment> text) {
    return (new Gson()).toJson(text);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the values from the form
    String msg = getUserComment(request);
    if (msg.startsWith(":") || msg.endsWith(":")) {
        return;
    } 

    Document doc = Document.newBuilder().setContent(msg).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();

    System.out.println("Score: " + String.valueOf(score));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", msg.substring(0, msg.indexOf(":")));
    commentEntity.setProperty("message", msg.substring(msg.indexOf(":") + 1));
    commentEntity.setProperty("score", String.valueOf(score));
    datastore.put(commentEntity);

    response.sendRedirect("/index.html");

  }

  private String getUserComment(HttpServletRequest request) {
    // Get the input from the form.
    String comment = getParameter(request, "comment", "");
    String name = getParameter(request, "name", "");
    if (name.length() == 0){
    System.err.println("Please enter a name");
    return ":";
    } else if (comment.length() == 0){
    System.err.println("Please enter a message");
    return name + ":";
    }
    return name + ":" + comment;
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
        return defaultValue;
    }
    return value;
  }
  
}
