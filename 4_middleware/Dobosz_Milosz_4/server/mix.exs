defmodule Subscriptions.Mixfile do
  use Mix.Project

  def project do
    [
      app: :subscriptions,
      version: "0.1.0",
      elixir: "~> 1.4",
      build_embedded: Mix.env() == :prod,
      start_permanent: Mix.env() == :prod,
      deps: deps()
    ]
  end

  def application do
    [mod: {SubscriptionsApp, []}, extra_applications: [:logger, :grpc]]
  end

  defp deps do
    [
      # {:grpc, "~> 0.5.0"},
      {:grpc, github: "elixir-grpc/grpc"},
      {:protobuf, "~> 0.11"},
      {:google_protos, "~> 0.3.0"},
      {:dialyxir, "~> 1.1", only: [:dev, :test], runtime: false}
    ]
  end
end
