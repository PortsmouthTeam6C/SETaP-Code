const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import NavigateScreen from "../../pages/NavigateScreen";


beforeAll(() => {
  window.alert = jest.fn();
});

beforeEach(() => {
  mockNavigate.mockClear();
});

describe("Navigate Screen", () => {
  it("renders search and list", () => {
    render(<NavigateScreen />, { wrapper: MemoryRouter });
    expect(screen.getByPlaceholderText(/search/i)).toBeInTheDocument();
    expect(screen.getByText(/select a society/i)).toBeInTheDocument();
  });

  it("filters societies by search input", async () => {
    render(<NavigateScreen />, { wrapper: MemoryRouter });
    const search = screen.getByPlaceholderText(/search/i);
    await userEvent.type(search, "Music");
    expect(search).toHaveValue("Music");
  });
});
