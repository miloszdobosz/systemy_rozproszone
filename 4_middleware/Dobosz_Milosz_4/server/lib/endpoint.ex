defmodule Helloworld.Endpoint do
  use GRPC.Endpoint

  # intercept GRPC.Server.Interceptors.Logger
  run(Events.Events.Server)
end
