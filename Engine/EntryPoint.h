#pragma once

extern Dragon::Application* Dragon::CreateApplication();

int main(int argc, char** argv) {
    auto app = Dragon::CreateApplication();
    app->Run();
    delete app;
}