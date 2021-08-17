#include "Application.hpp"

namespace Dragon {
  class Application {
  public:
    Application() {

    }

    ~Application() {
      delete m_window;
    }

    void run() {

    }
    
  private:
    Application(const Application&);
    Application& operator=(const Application&); 
    Window* m_window;
  };
}