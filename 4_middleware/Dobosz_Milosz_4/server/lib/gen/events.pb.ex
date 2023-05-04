defmodule Events.Topic do
  @moduledoc false

  use Protobuf, protoc_gen_elixir_version: "0.12.0", syntax: :proto3

  field(:id, 1, type: :int32)
  field(:name, 2, type: :string)
end

defmodule Events.Event do
  @moduledoc false

  use Protobuf, protoc_gen_elixir_version: "0.12.0", syntax: :proto3

  field(:topics, 1, repeated: true, type: Events.Topic)
  field(:message, 2, type: :string)
end

defmodule Events.Events.Service do
  @moduledoc false

  use GRPC.Service, name: "events.Events", protoc_gen_elixir_version: "0.12.0"

  rpc(:subscribe, Events.Topic, stream(Events.Event))

  rpc(:unsubscribe, Events.Topic, Events.Topic)
end

defmodule Events.Events.Stub do
  @moduledoc false

  use GRPC.Stub, service: Events.Events.Service
end
