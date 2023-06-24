defmodule Events.Events.Server do
  use GRPC.Server, service: Events.Events.Service

  def get_event(topics) do
    Process.sleep(:rand.uniform(1000))

    Events.Event.new(
      topics: Enum.take_random(topics, 2),
      message: "Hello"
    )
  end

  def get_event_stream do
    topics =
      1..10
      |> Enum.map(&Events.Topic.new(id: &1, name: "Name"))

    Stream.repeatedly(fn -> get_event(topics) end)
  end

  def subscribe(topic, stream) do
    %Events.Topic{
      id: id
    } = topic

    get_event_stream()
    |> Stream.filter(fn event ->
      id in Enum.map(event.topics, & &1.id)
    end)
    |> Enum.each(fn event ->
      IO.inspect(event)
      GRPC.Server.send_reply(
        stream,
        event
      )
    end)
  end
end
