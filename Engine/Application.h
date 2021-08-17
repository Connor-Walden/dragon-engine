#pragma once

namespace Dragon {

  class Application {
    public:
      Application();
      virtual ~Application();

      void Run();
  };

  Application* CreateApplication();
}