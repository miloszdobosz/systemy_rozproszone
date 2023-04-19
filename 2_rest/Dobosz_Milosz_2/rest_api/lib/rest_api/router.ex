defmodule RestApi.Router do
  use Plug.Router

  plug(:match)
  plug(:dispatch)

  get "/" do
    page = """
      <!DOCTYPE html>
      <html>
      <body>

      <h2>Get a random fact about a number</h2>

      <form action="/fact">
        <label for="number">Number:</label>
        <input type="number" id="number" name="number" value="42"><br>
        <input type="submit" value="Submit">
      </form> 

      </body>
      </html>
    """

    send_resp(conn, 200, page)
  end

  get "/fact" do
    number = Plug.Conn.fetch_query_params(conn).query_params["number"]

    {:ok, %HTTPoison.Response{body: body, status_code: 200}} =
      HTTPoison.get("http://numbersapi.com/" <> number)

    [_number | [_is | words]] = body |> String.split(" ", trim: true)
    options = words |> Enum.map(fn word -> "<option value=#{word}>#{word}</option>" end)

    page = """
      <!DOCTYPE html>
      <html>
      <body>

      <h2>Your random fact:</h2>
      #{body}
      
      <h2>Choose a word from above to explain:</h2>

      <form action="/definition">
        <label for="word">Word:</label>
        <select name="word" id="word">
          #{options}
        </select>
        <input type="submit" value="Submit">
      </form> 

      </body>
      </html>
    """

    send_resp(conn, 200, page)
  end

  get "/definition" do
    word = Plug.Conn.fetch_query_params(conn).query_params["word"]

    {:ok, %HTTPoison.Response{body: body, status_code: status}} =
      HTTPoison.get("http://api.dictionaryapi.dev/api/v2/entries/en/" <> word)

    {:ok, [body | _]} = body |> Jason.decode()

    case status do
      200 ->
        definitions =
          body["meanings"]
          |> Enum.map(fn meaning -> meaning["definitions"] end)
          |> Enum.reduce(&++/2)
          |> Enum.map(fn item -> "- #{item["definition"]}<br>" end)
          |> Enum.reduce(&<>/2)

        page = """
          <!DOCTYPE html>
          <html>

          <h2>Definitions of #{word}:</h2>
          <body>
            #{definitions}
          </body>
          </html>
        """

        send_resp(conn, 200, page)

      404 ->
        send_resp(conn, 404, "Word not found in the dictionary :(")
    end
  end

  match _ do
    send_resp(conn, 404, "Not Found")
  end
end
