defmodule Events.Events.Server do
  use GRPC.Server, service: Events.Events.Service

  def subscribe(topic, stream) do
    events = [
      Events.Event.new(
        topics: [Events.Topic.new(id: 0, name: "Name")],
        message: "Hello #{topic.name}"
      )
    ]

    events
    |> Enum.each(fn event ->
      GRPC.Server.send_reply(
        stream,
        event
      )
    end)
  end
end
